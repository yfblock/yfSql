package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Insert;
import io.github.yfblock.yfSql.Annotation.Select;

import java.util.ArrayList;

public class InsertTranslator extends TreeTranslator {

    private final Messager    messager;
    private final TreeMaker   treeMaker;
    private final Names       names;
    private final Insert      insert;
    private ArrayList<JCTree.JCExpression> params = new ArrayList<>();
    private String typeName;
    private boolean isSingle = false;

    public InsertTranslator(Messager messager, TreeMaker treeMaker, Names names, Insert insert) {
        this.messager   = messager;
        this.treeMaker  = treeMaker;
        this.names      = names;
        this.insert     = insert;
    }

    /**
     *  访问代码块
     * */
    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
        ArrayList<JCTree.JCStatement> newList   = new ArrayList<>();
        ArrayList<JCTree.JCExpression> args     = new ArrayList<>();
        // * first param
        if(params.size() > 0) {
            params.add(0, treeMaker.Literal(insert.value()));
            JCTree.JCMethodInvocation param = treeMaker.Apply(List.nil(),
                    treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                    List.from(params));
            args.add(param);
        } else {
            args.add(treeMaker.Literal(insert.value()));
        }
        args.add(treeMaker.Ident(names.fromString("sqlRunner")));
        newList.add(
                treeMaker.Return(
                        treeMaker.Apply(
                                List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("DataTableWrapper")),
                                        names.fromString("execute")
                                ),
                                List.from(args)
                        )
                )
        );
        jcBlock.stats = List.from(newList);
        messager.printMessage(Diagnostic.Kind.NOTE, List.from(newList).toString());
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree)  {
        String returnTypeName = tree.getReturnType().toString();
        if(returnTypeName.contains("<")) {
            typeName = returnTypeName.substring(returnTypeName.indexOf('<') + 1, returnTypeName.indexOf('>'));
            isSingle = false;
        } else {
            typeName = returnTypeName;
            isSingle = true;
        }
        for(JCTree.JCVariableDecl jcVariableDecl : tree.getParameters()) {
            if(jcVariableDecl.getType().toString().equals("String")) {
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
            params.add(treeMaker.Ident(jcVariableDecl.getName()));
        }
        super.visitMethodDef(tree);
    }
}
