package io.github.yfblock.yfSql.Processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import io.github.yfblock.yfSql.Annotation.DataRunner;

import javax.annotation.processing.Messager;
import java.util.ArrayList;

public class DataRunnerTranslator extends TreeTranslator {
    private final Messager    messager;
    private final TreeMaker   treeMaker;
    private final Names       names;
    private final DataRunner  dataRunner;

    public DataRunnerTranslator(Messager messager, TreeMaker treeMaker, Names names, DataRunner dataRunner) {
        this.messager   = messager;
        this.treeMaker  = treeMaker;
        this.names      = names;
        this.dataRunner = dataRunner;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        super.visitClassDef(tree);

        JCTree.JCVariableDecl jcVariableDecl = addSqlRunner();
        tree.defs = tree.defs.prepend(jcVariableDecl);
    }

    /**
     * add sqlRunner param
     * @return
     */
    private JCTree.JCVariableDecl addSqlRunner() {
        ArrayList<JCTree.JCExpression> args = new ArrayList<>();
        if(!dataRunner.useProperty()) {
            args.add(treeMaker.Literal(dataRunner.hostname()));
            args.add(treeMaker.Literal(dataRunner.port()));
            args.add(treeMaker.Literal(dataRunner.username()));
            args.add(treeMaker.Literal(dataRunner.password()));
            args.add(treeMaker.Literal(dataRunner.database()));
        }
        JCTree.JCExpression init = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString("MysqlRunner")),
                List.from(args),
                null);

        return treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString("sqlRunner"),
                treeMaker.Ident(names.fromString("SqlRunner")),
                init);
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree)  {
        super.visitMethodDef(tree);
    }

    /**
     * * 访问代码块
     * */
    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
    }
}
