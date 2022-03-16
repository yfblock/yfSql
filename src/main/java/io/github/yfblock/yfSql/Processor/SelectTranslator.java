package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.Select;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.ArrayList;

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
        ArrayList<JCTree.JCStatement> newList   = new ArrayList<>();
        ArrayList<JCTree.JCExpression> args     = new ArrayList<>();

        // function choose
        String functionName = isOneField?"executeQueryOneField":"executeQuery";
        functionName += isSingle?"Find":"";

        // * first param
        if(params.size() > 0) {
            params.add(0, treeMaker.Literal(select.value()));
            JCTree.JCMethodInvocation param = treeMaker.Apply(List.nil(),
                    treeMaker.Select(treeMaker.Ident(names.fromString("MessageFormat")), names.fromString("format")),
                    List.from(params));
            args.add(param);
        } else {
            args.add(treeMaker.Literal(select.value()));
        }
        args.add(treeMaker.Select(treeMaker.Ident(names.fromString(typeName)), names.fromString("class")));
        args.add(treeMaker.Ident(names.fromString("sqlRunner")));
        newList.add(
                treeMaker.Return(
                        treeMaker.Apply(
                                List.nil(),
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("DataTableWrapper")),
                                        names.fromString(functionName)
                                ),
                                List.from(args)
                        )
                )
        );
        jcBlock.stats = List.from(newList);
    }
}
