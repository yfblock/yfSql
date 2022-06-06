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
        String functionName = "execute";

        // * first param
        if (catches.length() > 0) {
            JCTree.JCTry jcTry = this.generateTryCatch(
                    this.generateTryBlock(delete.value(), functionName),
                    this.catches,
                    treeMaker.Block(0, jcBlock.stats)
            );

            jcBlock.stats = jcBlock.stats.prepend(jcTry);
        } else {
            jcBlock.stats = this.generateTryBlock(delete.value(), functionName).stats;
        }
    }
}
