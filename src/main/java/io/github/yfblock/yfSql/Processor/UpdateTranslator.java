package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Update;

import javax.annotation.processing.Messager;

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
        String functionName = "execute";
        if (catches.length() > 0) {
            JCTree.JCTry jcTry = this.generateTryCatch(
                    this.generateTryBlock(update.value(), functionName),
                    this.catches,
                    treeMaker.Block(0, jcBlock.stats)
            );

            jcBlock.stats = jcBlock.stats.prepend(jcTry);
        } else {
            jcBlock.stats = this.generateTryBlock(update.value(), functionName).stats;
        }
    }
}
