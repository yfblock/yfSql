package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Delete;
import io.github.yfblock.yfSql.Annotation.Update;

import javax.annotation.processing.Messager;
import java.util.ArrayList;

public class UpdateTranslator extends BaseTranslator {

    private final Update        update;

    public UpdateTranslator(Messager messager, TreeMaker treeMaker, Names names, Update update) {
        super(messager, treeMaker, names);
        this.update     = update;
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
            params.add(0, treeMaker.Literal(update.value()));
            JCTree.JCMethodInvocation param = treeMaker.Apply(List.nil(),
                    treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                    List.from(params));
            args.add(param);
        } else {
            args.add(treeMaker.Literal(update.value()));
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
