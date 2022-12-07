package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Delete;

import javax.annotation.processing.Messager;

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

        super.executeVisitBlock(jcBlock, delete.value(), "execute");
    }
}
