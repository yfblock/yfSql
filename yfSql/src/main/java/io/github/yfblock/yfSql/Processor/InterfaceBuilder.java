package io.github.yfblock.yfSql.Processor;

import io.github.yfblock.yfSql.Annotation.*;
import io.github.yfblock.yfSql.Runner.SqlRunner;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InterfaceBuilder {

    // The element will be handled
    private TypeElement element;
    private Messager messager;
    private String interfaceName;
    private PrintWriter writer;

    /**
     * Constructor
     * @param element the class Element with @DataRunner
     * @param messager write message to compiler, such as NOTE, WARNING, ERROR
     * @param writer source file writer
     */
    public InterfaceBuilder(TypeElement element, Messager messager, PrintWriter writer) {
        this.element  = element;
        this.messager = messager;
        this.writer   = writer;
        this.interfaceName = element.getSimpleName().toString();
    }

    /**
     *
     * @param sql the sql will be built
     * @param params interface method params
     * @return the statement will be used
     */
    protected String buildSqlAndParams(String sql, String params) {
        System.out.println(params);
        // replace sql to correct String
        // transfer " to \" in the sql, ' to '' for MessageFormat
        sql = sql.replaceAll("\"", "\\\\\"").replaceAll("'", "''");
        params = params.trim();
        if(params == null || params.equals("")) {
            return "\"" + sql + "\"";
        } else {
            return "MessageFormat.format(\"" + sql + "\", " + params + ")";
        }
    }

    /**
     * generate sql and related code according to interface method and its annotation
     * @param executableElement interface method Element with @Select, @Insert or ....
     */
    protected void buildSql(ExecutableElement executableElement) {
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

    /**
     * get value of runner
     * @return the class name of runner value
     */
    protected String getRunnerClassValue() {
        DataRunner dataRunner = this.element.getAnnotation(DataRunner.class);
        try {
            return dataRunner.value().getTypeName();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror().toString();
        }
    }

    /**
     * write package String to writer
     */
    protected void writePackage() {
        // package
        writer.println("package " + this.element.getEnclosingElement().toString() + ";");
    }

    /**
     * write interface methods to writer
     */
    protected void writeInterfaceMethods() {
        this.element.getEnclosedElements().stream()
            .map(ele -> (ExecutableElement) ele)
            .forEach(ele -> {
                // parameters build
                ArrayList<String> parameters = new ArrayList<>();
                for(VariableElement variableElement : ele.getParameters()) {
                    parameters.add(variableElement.asType() + " " + variableElement.getSimpleName());
                }
                // parameters build end

                // get return type
                String returnType = ele.getReturnType().toString();

                // function header
                writer.write(MessageFormat.format(
                        "public {0} {1}({2})",
                        returnType,
                        ele.getSimpleName(),
                        String.join(",", parameters)
                ));

                // write exceptions
                if (ele.getThrownTypes().size() > 0) {
                    List<String> exceptions = ele.getThrownTypes().stream()
                            .map(m -> m.toString()).collect(Collectors.toList());
                    writer.write(" throws " + String.join(",", exceptions));
                }

                // write start of content
                writer.println(" {");

                // function content build statement for sql
                this.buildSql(ele);

                // function end
                writer.println("}");
            });
    }

    /**
     * write imports String to writer
     */
    protected void writeImports() {
        writer.println("import io.github.yfblock.yfSql.Runner.SqlRunner;");
        writer.println("import io.github.yfblock.yfSql.Sql.DataTableWrapper;");
        writer.println("import java.sql.SQLException;");
        writer.println("import java.text.MessageFormat;");
    }

    /**
     * write fields to writer
     */
    protected void writeFields() {
        writer.println("private SqlRunner sqlRunner;");
    }

    /**
     * write constructors to writer, NOTE: this method will write multiple constructor
     */
    protected void writeConstructors() {
        // no-args constructor
        writer.println(MessageFormat.format("public {0}Impl() '{'", interfaceName));
        String targetClzName = this.getRunnerClassValue();
        if (!targetClzName.equals(SqlRunner.class.getTypeName())) {
            writer.println("this.sqlRunner = new " + targetClzName + "();");
        } else {
            // print warning
            this.messager.printMessage(Diagnostic.Kind.WARNING,
                    "Class " + this.element.getSimpleName() + " Not config the correct runner, " +
                            "if you will pass runner by yourself, ignore please");
        }
        writer.println("}");

        // just SqlRunner as arg constructor
        writer.println(MessageFormat.format("public {0}Impl(SqlRunner sqlRunner) '{'", interfaceName));
        writer.println("this.sqlRunner = sqlRunner;");
        writer.println("}");
    }

    /**
     * write class and content to writer
     */
    protected void writeClass() {
        writer.println(MessageFormat.format("public class {0}Impl implements {0} '{'", interfaceName));
        this.writeFields();
        this.writeConstructors();
        this.writeInterfaceMethods();
        writer.println("}");
    }

    /**
     * write the target class and its dependencies string to writer
     */
    public void build() {
        this.writePackage();
        this.writeImports();
        this.writeClass();
    }
}
