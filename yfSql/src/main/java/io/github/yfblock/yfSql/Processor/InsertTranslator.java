package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Insert;

import javax.annotation.processing.Messager;

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

        super.executeVisitBlock(jcBlock, insert.value(), "execute");
    }
}
