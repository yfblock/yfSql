package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Delete;
import io.github.yfblock.yfSql.Annotation.Insert;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.ArrayList;

public class DeleteTranslator extends BaseTranslator {

    private final Delete        delete;

    public DeleteTranslator(Messager messager, TreeMaker treeMaker, Names names, Delete delete) {
        super(messager, treeMaker, names);
        this.delete     = delete;
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
            params.add(0, treeMaker.Literal(delete.value()));
            JCTree.JCMethodInvocation param = treeMaker.Apply(List.nil(),
                    treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                    List.from(params));
            args.add(param);
        } else {
            args.add(treeMaker.Literal(delete.value()));
        }
        args.add(treeMaker.Ident(names.fromString("sqlRunner")));
        JCTree.JCMethodInvocation jcMethodInvocation = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(names.fromString("DataTableWrapper")),
                        names.fromString("execute")
                ),
                List.from(args)
        );
        newList.add(treeMaker.Exec(jcMethodInvocation));
        jcBlock.stats = List.from(newList);
    }
}
