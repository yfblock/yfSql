package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Select;

import javax.annotation.processing.Messager;

public class SelectTranslator extends BaseTranslator {

    private final Select      select;

    public SelectTranslator(Messager messager, TreeMaker treeMaker, Names names, Select select) {
        super(messager, treeMaker, names);
        this.select     = select;
    }

    /**
     *  访问代码块
     * */
    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);

        // function choose
        String functionName = isOneField?"executeQueryOneField":"executeQuery";
        functionName += isSingle?"Find":"";

        super.executeVisitBlock(jcBlock, select.value(), functionName);
    }
}
