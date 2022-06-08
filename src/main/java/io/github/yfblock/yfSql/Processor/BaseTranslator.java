package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Utils.ParamUtil;

import javax.annotation.processing.Messager;
import java.util.ArrayList;

public class
BaseTranslator extends TreeTranslator {
    protected final Messager messager;
    protected final TreeMaker treeMaker;
    protected final Names names;
    protected ArrayList<JCTree.JCExpression> params = new ArrayList<>();
    protected String typeName;
    protected boolean isSingle = false;
    protected boolean isOneField = false;
    protected ArrayList<String> catchesStr = new ArrayList<>();
    protected List<JCTree.JCCatch> catches;

    public BaseTranslator(Messager messager, TreeMaker treeMaker, Names names) {
        this.messager = messager;
        this.treeMaker = treeMaker;
        this.names = names;

        // init catches
        catchesStr.add("SQLException");
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        String returnTypeName = tree.getReturnType().toString();
        if (returnTypeName.contains("<")) {
            typeName = returnTypeName.substring(returnTypeName.indexOf('<') + 1, returnTypeName.indexOf('>'));
            isOneField = ParamUtil.isBasicType(typeName);
            isSingle = false;
        } else {
            typeName = returnTypeName;
            isOneField = ParamUtil.isBasicType(returnTypeName);
            isSingle = true;
        }
        // get throws
        for(JCTree.JCExpression jcExpression : tree.getThrows()) {
            int index = this.catchesStr.indexOf(jcExpression.toString());
            if(index != -1) this.catchesStr.remove(index);
        }
        // generate catches
        ArrayList<JCTree.JCCatch> catches1 = new ArrayList<>();
        for(String catchTypeName : catchesStr) {
            catches1.add(generateCatch(catchTypeName));
        }
        this.catches = List.from(catches1);

        // get variables
        for (JCTree.JCVariableDecl jcVariableDecl : tree.getParameters()) {
            if (jcVariableDecl.getType().toString().equals("String")) {
                ArrayList<JCTree.JCExpression> args = new ArrayList<>();
                args.add(treeMaker.Literal("\"{0}\""));
                args.add(treeMaker.Ident(jcVariableDecl.getName()));
                params.add(treeMaker.Apply(
                        List.nil(),
                        treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                        List.from(args)
                ));
                continue;
            }
            ArrayList<JCTree.JCExpression> args = new ArrayList<>();
            args.add(treeMaker.Ident(jcVariableDecl.getName()));
            params.add(
                    treeMaker.Apply(
                            List.nil(),
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString("ParamUtil")),
                                    names.fromString("targetToString")
                            ),
                            List.from(args)));
        }
        super.visitMethodDef(tree);
        System.out.println(tree);
    }

    // generate try catch
    public JCTree.JCCatch generateCatch(String exceptionName) {
        JCTree.JCExpression jcExpression = treeMaker.Ident(names.fromString(exceptionName));
        JCTree.JCVariableDecl var = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER),
                names.fromString("e"),
                jcExpression,
                null
        );
        ArrayList<JCTree.JCStatement> newList   = new ArrayList<>();
        JCTree.JCMethodInvocation jcMethodInvocation = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(names.fromString("e")),
                        names.fromString("printStackTrace")
                ),
                List.nil()
        );
        newList.add(treeMaker.Exec(jcMethodInvocation));
        JCTree.JCBlock block = treeMaker.Block(0, List.from(newList));
        return treeMaker.Catch(var, block);
    }

    // generate empty block
    public JCTree.JCBlock generateEmptyBlock() {
        return treeMaker.Block(0, List.nil());
    }

    public JCTree.JCTry generateTryCatch(JCTree.JCBlock jcBlock, List<JCTree.JCCatch> catches, JCTree.JCBlock defaultBlock) {
        return treeMaker.Try(jcBlock, catches, null);
    }

    public JCTree.JCBlock generateTryBlock(String sql, String functionName) {
        ArrayList<JCTree.JCStatement> newList   = new ArrayList<>();
        ArrayList<JCTree.JCExpression> args     = new ArrayList<>();

        // * first param
        if(params.size() > 0) {
            params.add(0, treeMaker.Literal(sql));
            JCTree.JCMethodInvocation param = treeMaker.Apply(List.nil(),
                    treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                    List.from(params));
            args.add(param);
        } else {
            args.add(treeMaker.Literal(sql));
        }
        args.add(treeMaker.Select(treeMaker.Ident(names.fromString(typeName)), names.fromString("class")));
        args.add(treeMaker.Ident(names.fromString("sqlRunner")));
        newList.add(
                treeMaker.Return(
                        treeMaker.Apply(
                                List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("DataTableWrapper")),
                                        names.fromString(functionName)
                                ),
                                List.from(args)
                        )
                )
        );
        return treeMaker.Block(0, List.from(newList));
    }

    /**
     *  访问执行代码块
     * */
    protected void executeVisitBlock(JCTree.JCBlock jcBlock, String sql,String functionName) {
        // * first param
        if (catches.length() > 0) {
            JCTree.JCTry jcTry = this.generateTryCatch(
                    this.generateTryBlock(sql, functionName),
                    this.catches,
                    treeMaker.Block(0, jcBlock.stats)
            );

            jcBlock.stats = jcBlock.stats.prepend(jcTry);
        } else {
            jcBlock.stats = this.generateTryBlock(sql, functionName).stats;
        }
    }
}
