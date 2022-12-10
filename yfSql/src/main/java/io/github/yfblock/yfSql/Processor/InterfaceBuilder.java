package io.github.yfblock.yfSql.Processor;

import io.github.yfblock.yfSql.Annotation.*;

import javax.lang.model.element.*;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;

public class InterfaceBuilder {

    // The element will be handled
    private TypeElement element;

    public InterfaceBuilder(TypeElement element) {
        this.element = element;
    }

    protected String buildSqlAndParams(String sql, String params) {
        System.out.println(params);
        // replace sql to correct String
        // transfer " to \" in the sql
        sql = sql.replaceAll("\"", "\\\\\"");
        params = params.trim();
        if(params == null || params.equals("")) {
            return "\"" + sql + "\"";
        } else {
            return "MessageFormat.format(\"" + sql + "\", " + params + ")";
        }
    }

    protected void buildSql(PrintWriter writer, ExecutableElement executableElement) {
        System.out.println("----------- judge for function " + executableElement + "-----------");

        // 判断是否需要 return
        if (!executableElement.getReturnType().toString().equals("void")) {
            writer.print("return ");
        }

        ArrayList parameters = new ArrayList();
        for(VariableElement variableElement : executableElement.getParameters()) {
            parameters.add(variableElement.getSimpleName());
        }
        String parameterString = String.join(",", parameters);

        Select select = executableElement.getAnnotation(Select.class);
        if(select != null) {
            String returnType = executableElement.getReturnType().toString();
            writer.println(MessageFormat.format(
                    "DataTableWrapper.executeQuery({0}, {1}.class, this.sqlRunner);",
                    this.buildSqlAndParams(select.value(), parameterString),
                    returnType.substring(returnType.indexOf('<') + 1, returnType.lastIndexOf('>'))
            ));
            return;
        }
        Insert insert = executableElement.getAnnotation(Insert.class);
        if(insert != null) {
            writer.println(MessageFormat.format(
                    "DataTableWrapper.execute({0}, this.sqlRunner);",
                    this.buildSqlAndParams(insert.value(), parameterString)
            ));
            return;
        }
        Delete delete = executableElement.getAnnotation(Delete.class);
        if(delete != null) {
            writer.println(MessageFormat.format(
                    "DataTableWrapper.execute({0}, this.sqlRunner);",
                    this.buildSqlAndParams(delete.value(), parameterString)
            ));
            return;
        }
        Update update = executableElement.getAnnotation(Update.class);
        if(update != null) {
            writer.println(MessageFormat.format(
                    "DataTableWrapper.execute({0}, this.sqlRunner);",
                    this.buildSqlAndParams(update.value(), parameterString)
            ));
            return;
        }
        Find find = executableElement.getAnnotation(Find.class);
        System.out.println("----------- end for function " + executableElement + "-----------");
    }

    protected void buildInterfaceMethods(PrintWriter writer) {
        for(Element ele : this.element.getEnclosedElements()) {
            ExecutableElement executableElement = (ExecutableElement) ele;

            // parameters build
            ArrayList<String> parameters = new ArrayList<>();
            for(VariableElement variableElement : executableElement.getParameters()) {
                parameters.add(variableElement.asType() + " " + variableElement.getSimpleName());
            }
            // parameters build end

            // get return type
            String returnType = executableElement.getReturnType().toString();

            // function header
            writer.println(MessageFormat.format("public {0} {1}({2}) throws SQLException '{'", returnType,
                    executableElement.getSimpleName(), String.join(",", parameters)));

            // function content build statement for sql
            this.buildSql(writer, executableElement);

            // function end
            writer.println("}");
        }
    }

    /**
     * write the target class and its dependencies string to writer
     * @param writer the PrintWriter that got from createSource
     */
    public void build(PrintWriter writer) {
        String interfaceName = element.getSimpleName().toString();
        // package
        writer.println("package " + this.element.getEnclosingElement().toString() + ";");

        // imports
        writer.println("import io.github.yfblock.yfSql.Runner.SqlRunner;");
        writer.println("import io.github.yfblock.yfSql.Sql.DataTableWrapper;");
        writer.println("import java.sql.SQLException;");
        writer.println("import java.text.MessageFormat;");
        // imports end

        // class start
        writer.println(MessageFormat.format("public class {0}Impl implements {0} '{'", interfaceName));

        // fields
        writer.println("private SqlRunner sqlRunner;");
        // fields end

        // constructor TODO: add sqlRunner or its child as the parameter
        writer.println(MessageFormat.format("public {0}Impl(SqlRunner sqlRunner) '{'", interfaceName));
        writer.println("this.sqlRunner = sqlRunner;");
        writer.println("}");
        // constructor end

        // build interface methods
        this.buildInterfaceMethods(writer);
        // build interface methods end

        writer.println("}");
        // class end
    }
}
