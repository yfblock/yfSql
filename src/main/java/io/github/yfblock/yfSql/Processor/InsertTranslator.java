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

public class InsertTranslator extends BaseTranslator {

    private final Insert      insert;

    public InsertTranslator(Messager messager, TreeMaker treeMaker, Names names, Insert insert) {
        super(messager, treeMaker, names);
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
    }
}
