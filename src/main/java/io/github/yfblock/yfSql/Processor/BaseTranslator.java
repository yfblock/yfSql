package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Utils.ParamUtil;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
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

    public BaseTranslator(Messager messager, TreeMaker treeMaker, Names names) {
        this.messager = messager;
        this.treeMaker = treeMaker;
        this.names = names;
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
    }
}
