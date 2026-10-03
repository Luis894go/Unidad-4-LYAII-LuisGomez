package com.luis.codelab;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class Controller {

    @FXML
    private TextArea source;

    @FXML
    private TextArea tokens;

    @FXML
    private TextArea errors;

    @FXML
    private TextArea intermediate;

    @FXML
    private TextArea registers;

    @FXML
    private TextArea assembly;
    @FXML
    private TextArea machine;
    @FXML
    private TextArea memory;
    @FXML
    private TableView<Symbol> symbols;
    @FXML
    private TableColumn<Symbol, String> nameCol;
    @FXML
    private TableColumn<Symbol, String> typeCol;
    @FXML
    private TableColumn<Symbol, String> valueCol;
    private final MiniCompiler compiler = new MiniCompiler();
    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().name())
        );
        typeCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().type())
        );
        valueCol.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().value())
        );
        loadExample();
    }
    @FXML
    private void compile() {
        CompileResult result = compiler.compile(source.getText());

        tokens.setText(
                String.join("\n", result.tokens())
        );

        errors.setText(
                String.join("\n", result.errors())
        );

        intermediate.setText(
                String.join("\n", result.intermediate())
        );

        registers.setText(
                String.join("\n", result.registers())
        );

        assembly.setText(
                String.join("\n", result.assembly())
        );

        machine.setText(
                String.join("\n", result.machine())
        );

        memory.setText(
                String.join("\n", result.memory())
        );

        symbols.getItems().clear();
        symbols.getItems().addAll(result.symbols());
    }

    @FXML
    private void loadExample() {

        source.setText("""
                int numero = 10;
                int valor = 20;
                int resultado = numero + valor;

                print(resultado);
                """);

        clearResults();
    }

    @FXML
    private void loadErrorExample() {

        source.setText("""
                int numero = 10;
                int numero = 30;

                resultado = numero + desconocida;
                int division = 10 / 0;

                print(resultado);
                """);

        clearResults();
    }

    @FXML
    private void clear() {
        source.clear();
        clearResults();
    }

    private void clearResults() {

        tokens.clear();
        errors.clear();
        intermediate.clear();
        registers.clear();
        assembly.clear();
        machine.clear();
        memory.clear();

        symbols.getItems().clear();
    }
}