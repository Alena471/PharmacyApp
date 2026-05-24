package pharmacy;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

public class Main extends Application {

    private TabPane tabPane = new TabPane();
    private Label statusLabel = new Label("Готово");
    private ComboBox<String> dbSelector = new ComboBox<>();

    // Данные
    private ObservableList<Medicine> medicineList = FXCollections.observableArrayList();
    private ObservableList<Batch> batchList = FXCollections.observableArrayList();
    private ObservableList<Batch> stockList = FXCollections.observableArrayList();

    // Таблицы
    private TableView<Medicine> medicineTable = new TableView<>();
    private TableView<Batch> batchTable = new TableView<>();
    private TableView<Batch> stockTable = new TableView<>();

    // Корзина
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private TableView<CartItem> cartTable = new TableView<>();
    private Label totalLabel = new Label("Итого: 0.00 руб");
    private ComboBox<String> customerCombo = new ComboBox<>();
    private ComboBox<String> employeeCombo = new ComboBox<>();
    private ObservableList<String> customerNames = FXCollections.observableArrayList();
    private ObservableList<String> employeeNames = FXCollections.observableArrayList();
    private ObservableList<Batch> availableBatches = FXCollections.observableArrayList();
    private ComboBox<String> batchCombo = new ComboBox<>();
    private Spinner<Integer> quantitySpinner = new Spinner<>(1, 1000, 1);
    private Label batchPriceLabel = new Label("Цена: 0.00 руб");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Аптека - Учёт лекарственных средств");

        // Панель выбора БД
        dbSelector.getItems().addAll("postgresql", "sqlserver");
        dbSelector.setValue(DatabaseConnection.getDatabaseType());
        dbSelector.setOnAction(e -> {
            DatabaseConnection.setDatabaseType(dbSelector.getValue());
            refreshAll();
            statusLabel.setText("Переключено на " + dbSelector.getValue());
        });

        Button btnRefreshAll = new Button("Обновить все");
        btnRefreshAll.setOnAction(e -> refreshAll());

        HBox topPanel = new HBox(10, new Label("База данных:"), dbSelector, btnRefreshAll);
        topPanel.setPadding(new Insets(10));

        // Создаём вкладки
        createMedicinesTab();
        createBatchesTab();
        createStockTab();
        createSalesTab();
        createReportTab();

        BorderPane root = new BorderPane();
        root.setTop(topPanel);
        root.setCenter(tabPane);
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshAll();
    }

    private void refreshAll() {
        loadMedicines();
        loadBatches();
        loadStock();
        loadCustomersAndEmployees();
        statusLabel.setText("Обновлено. БД: " + DatabaseConnection.getDatabaseType());
    }

    // ==================== ВКЛАДКА: ЛЕКАРСТВА ====================

    private void createMedicinesTab() {
        Tab tab = new Tab("Лекарства");

        TableColumn<Medicine, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        TableColumn<Medicine, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Medicine, String> dosageCol = new TableColumn<>("Дозировка");
        dosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        TableColumn<Medicine, String> formCol = new TableColumn<>("Форма выпуска");
        formCol.setCellValueFactory(new PropertyValueFactory<>("releaseForm"));

        medicineTable.getColumns().addAll(idCol, nameCol, dosageCol, formCol);
        medicineTable.setItems(medicineList);

        Button btnAdd = new Button("Добавить");
        Button btnEdit = new Button("Редактировать");
        Button btnDelete = new Button("Удалить");

        btnAdd.setOnAction(e -> showMedicineDialog(null));
        btnEdit.setOnAction(e -> {
            Medicine selected = medicineTable.getSelectionModel().getSelectedItem();
            if (selected != null) showMedicineDialog(selected);
        });
        btnDelete.setOnAction(e -> deleteMedicine());

        HBox buttons = new HBox(10, btnAdd, btnEdit, btnDelete);
        VBox vbox = new VBox(10, medicineTable, buttons);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }

    private void loadMedicines() {
        medicineList.clear();
        try {
            medicineList.addAll(DatabaseService.getAllMedicines());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить лекарства: " + e.getMessage());
        }
    }

    private void showMedicineDialog(Medicine medicine) {
        Stage dialog = new Stage();
        dialog.setTitle(medicine == null ? "Добавить лекарство" : "Редактировать лекарство");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField dosageField = new TextField();
        TextField formField = new TextField();
        ComboBox<String> manufacturerCombo = new ComboBox<>();
        CheckBox prescriptionCheck = new CheckBox();

        try {
            manufacturerCombo.getItems().addAll(DatabaseService.getAllManufacturers());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (medicine != null) {
            nameField.setText(medicine.getName());
            dosageField.setText(medicine.getDosage());
            formField.setText(medicine.getReleaseForm());
            manufacturerCombo.setValue(medicine.getManufacturerName());
            prescriptionCheck.setSelected(medicine.isPrescriptionRequired());
        }

        grid.add(new Label("Название:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Дозировка:"), 0, 1);
        grid.add(dosageField, 1, 1);
        grid.add(new Label("Форма выпуска:"), 0, 2);
        grid.add(formField, 1, 2);
        grid.add(new Label("Производитель:*"), 0, 3);
        grid.add(manufacturerCombo, 1, 3);
        grid.add(new Label("Рецептурный:"), 0, 4);
        grid.add(prescriptionCheck, 1, 4);

        Button btnSave = new Button("Сохранить");
        btnSave.setOnAction(e -> {
            // Проверки ввода
            if (nameField.getText().trim().isEmpty()) {
                showAlert("Ошибка", "Введите название лекарства");
                return;
            }
            if (manufacturerCombo.getValue() == null) {
                showAlert("Ошибка", "Выберите производителя");
                return;
            }

            try {
                int manufacturerId = DatabaseService.getManufacturerId(manufacturerCombo.getValue());
                if (manufacturerId == -1) {
                    showAlert("Ошибка", "Производитель не найден");
                    return;
                }
                if (medicine == null) {
                    Medicine newMedicine = new Medicine(0, nameField.getText().trim(),
                            dosageField.getText().trim(), formField.getText().trim(),
                            manufacturerId, manufacturerCombo.getValue(), prescriptionCheck.isSelected());
                    DatabaseService.addMedicine(newMedicine);
                } else {
                    Medicine updatedMedicine = new Medicine(medicine.getMedicineId(), nameField.getText().trim(),
                            dosageField.getText().trim(), formField.getText().trim(),
                            manufacturerId, manufacturerCombo.getValue(), prescriptionCheck.isSelected());
                    DatabaseService.updateMedicine(updatedMedicine);
                }
                dialog.close();
                loadMedicines();
                statusLabel.setText("Лекарство сохранено");
            } catch (SQLException ex) {
                showAlert("Ошибка", "Не удалось сохранить: " + ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, grid, btnSave);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 450, 350);
        dialog.setScene(scene);
        dialog.show();
    }

    private void deleteMedicine() {
        Medicine selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            if (!DatabaseService.canDeleteMedicine(selected.getMedicineId())) {
                showAlert("Ошибка", "Нельзя удалить лекарство. Есть партии на складе!");
                return;
            }
        } catch (SQLException e) {
            showAlert("Ошибка", e.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setContentText("Удалить лекарство \"" + selected.getName() + "\"?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseService.deleteMedicine(selected.getMedicineId());
                loadMedicines();
                statusLabel.setText("Лекарство удалено");
            } catch (SQLException e) {
                showAlert("Ошибка", e.getMessage());
            }
        }
    }

    // ==================== ВКЛАДКА: ПАРТИИ ====================

    private void createBatchesTab() {
        Tab tab = new Tab("Партии");

        TableColumn<Batch, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        TableColumn<Batch, String> numberCol = new TableColumn<>("Номер");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        TableColumn<Batch, String> medicineCol = new TableColumn<>("Лекарство");
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<Batch, Integer> quantityCol = new TableColumn<>("Кол-во");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Batch, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        TableColumn<Batch, LocalDate> expirationCol = new TableColumn<>("Срок годности");
        expirationCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        batchTable.getColumns().addAll(idCol, numberCol, medicineCol, quantityCol, priceCol, expirationCol);
        batchTable.setItems(batchList);

        Button btnAdd = new Button("Приход товара");
        btnAdd.setOnAction(e -> showBatchDialog());

        VBox vbox = new VBox(10, batchTable, btnAdd);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }

    private void loadBatches() {
        batchList.clear();
        try {
            batchList.addAll(DatabaseService.getAllBatches());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить партии: " + e.getMessage());
        }
    }

    private void showBatchDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Приход товара");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> medicineCombo = new ComboBox<>();
        ComboBox<String> supplierCombo = new ComboBox<>();
        TextField batchNumberField = new TextField();
        DatePicker deliveryDatePicker = new DatePicker(LocalDate.now());
        DatePicker expirationDatePicker = new DatePicker(LocalDate.now().plusYears(1));
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10000, 100);
        TextField purchasePriceField = new TextField();
        TextField salePriceField = new TextField();

        try {
            for (Medicine m : DatabaseService.getAllMedicines()) {
                medicineCombo.getItems().add(m.getName());
            }
            supplierCombo.getItems().addAll(DatabaseService.getAllSuppliers());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        grid.add(new Label("Лекарство:*"), 0, 0);
        grid.add(medicineCombo, 1, 0);
        grid.add(new Label("Поставщик:*"), 0, 1);
        grid.add(supplierCombo, 1, 1);
        grid.add(new Label("Номер партии:*"), 0, 2);
        grid.add(batchNumberField, 1, 2);
        grid.add(new Label("Дата поставки:*"), 0, 3);
        grid.add(deliveryDatePicker, 1, 3);
        grid.add(new Label("Срок годности:*"), 0, 4);
        grid.add(expirationDatePicker, 1, 4);
        grid.add(new Label("Количество:*"), 0, 5);
        grid.add(quantitySpinner, 1, 5);
        grid.add(new Label("Закупочная цена:*"), 0, 6);
        grid.add(purchasePriceField, 1, 6);
        grid.add(new Label("Цена продажи:*"), 0, 7);
        grid.add(salePriceField, 1, 7);

        Button btnSave = new Button("Сохранить");
        btnSave.setOnAction(e -> {
            if (medicineCombo.getValue() == null) {
                showAlert("Ошибка", "Выберите лекарство");
                return;
            }
            if (supplierCombo.getValue() == null) {
                showAlert("Ошибка", "Выберите поставщика");
                return;
            }
            if (batchNumberField.getText().trim().isEmpty()) {
                showAlert("Ошибка", "Введите номер партии");
                return;
            }
            if (expirationDatePicker.getValue().isBefore(deliveryDatePicker.getValue())) {
                showAlert("Ошибка", "Срок годности должен быть позже даты поставки");
                return;
            }
            try {
                double purchasePrice = Double.parseDouble(purchasePriceField.getText());
                double salePrice = Double.parseDouble(salePriceField.getText());
                if (purchasePrice <= 0 || salePrice <= 0) {
                    showAlert("Ошибка", "Цены должны быть больше 0");
                    return;
                }

                int medicineId = DatabaseService.getMedicineId(medicineCombo.getValue());
                int supplierId = DatabaseService.getSupplierId(supplierCombo.getValue());

                DatabaseService.addBatch(medicineId, supplierId, batchNumberField.getText().trim(),
                        deliveryDatePicker.getValue(), expirationDatePicker.getValue(),
                        quantitySpinner.getValue(), purchasePrice, salePrice);

                dialog.close();
                loadBatches();
                loadStock();
                loadCustomersAndEmployees();
                statusLabel.setText("Партия добавлена");
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректные цены");
            } catch (SQLException ex) {
                showAlert("Ошибка", "Не удалось добавить партию: " + ex.getMessage());
            }
        });

        VBox vbox = new VBox(10, grid, btnSave);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 450, 450);
        dialog.setScene(scene);
        dialog.show();
    }

    // ==================== ВКЛАДКА: ОСТАТКИ ====================

    private void createStockTab() {
        Tab tab = new Tab("Остатки");

        TableColumn<Batch, String> medicineCol = new TableColumn<>("Лекарство");
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<Batch, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<Batch, Double> priceCol = new TableColumn<>("Цена продажи");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        TableColumn<Batch, LocalDate> expirationCol = new TableColumn<>("Срок годности");
        expirationCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        stockTable.getColumns().addAll(medicineCol, quantityCol, priceCol, expirationCol);
        stockTable.setItems(stockList);

        VBox vbox = new VBox(10, stockTable);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }

    private void loadStock() {
        stockList.clear();
        try {
            stockList.addAll(DatabaseService.getStock());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить остатки: " + e.getMessage());
        }
    }

    // ==================== ВКЛАДКА: ПРОДАЖИ ====================

    private void createSalesTab() {
        Tab tab = new Tab("Продажа");

        // Выбор клиента и сотрудника
        customerCombo.setItems(customerNames);
        employeeCombo.setItems(employeeNames);

        GridPane topGrid = new GridPane();
        topGrid.setPadding(new Insets(10));
        topGrid.setHgap(10);
        topGrid.setVgap(10);
        topGrid.add(new Label("Клиент:*"), 0, 0);
        topGrid.add(customerCombo, 1, 0);
        topGrid.add(new Label("Сотрудник:*"), 2, 0);
        topGrid.add(employeeCombo, 3, 0);

        // Выбор товара
        batchCombo.setOnAction(e -> updateBatchPrice());

        GridPane itemGrid = new GridPane();
        itemGrid.setPadding(new Insets(10));
        itemGrid.setHgap(10);
        itemGrid.setVgap(10);
        itemGrid.add(new Label("Товар:"), 0, 0);
        itemGrid.add(batchCombo, 1, 0);
        itemGrid.add(new Label("Количество:"), 2, 0);
        itemGrid.add(quantitySpinner, 3, 0);
        itemGrid.add(batchPriceLabel, 4, 0);

        Button btnAddToCart = new Button("Добавить в корзину");
        btnAddToCart.setOnAction(e -> addToCart());

        // Таблица корзины
        TableColumn<CartItem, String> itemNameCol = new TableColumn<>("Товар");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        TableColumn<CartItem, Integer> itemQtyCol = new TableColumn<>("Кол-во");
        itemQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<CartItem, Double> itemPriceCol = new TableColumn<>("Цена");
        itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<CartItem, Double> itemTotalCol = new TableColumn<>("Сумма");
        itemTotalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        cartTable.getColumns().addAll(itemNameCol, itemQtyCol, itemPriceCol, itemTotalCol);
        cartTable.setItems(cartList);

        Button btnRemoveFromCart = new Button("Удалить из корзины");
        btnRemoveFromCart.setOnAction(e -> {
            CartItem selected = cartTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartList.remove(selected);
                updateTotal();
            }
        });

        Button btnCompleteSale = new Button("Оформить продажу");
        btnCompleteSale.setOnAction(e -> completeSale());

        HBox cartButtons = new HBox(10, btnRemoveFromCart, btnCompleteSale);

        VBox vbox = new VBox(10, topGrid, new Separator(), itemGrid, btnAddToCart,
                new Separator(), cartTable, cartButtons, totalLabel);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }

    private void loadCustomersAndEmployees() {
        customerNames.clear();
        employeeNames.clear();
        availableBatches.clear();
        batchCombo.getItems().clear();

        try {
            customerNames.addAll(DatabaseService.getAllCustomers());
            employeeNames.addAll(DatabaseService.getAllEmployees());
            availableBatches.addAll(DatabaseService.getAvailableBatches());

            for (Batch b : availableBatches) {
                String displayText = b.getBatchId() + " - " + b.getMedicineName() + " (остаток: " + b.getQuantity() + ")";
                batchCombo.getItems().add(displayText);
            }

            if (batchCombo.getItems().isEmpty()) {
                batchCombo.getItems().add("Нет доступных товаров");
                batchCombo.setDisable(true);
            } else {
                batchCombo.setDisable(false);
            }

        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    private void updateBatchPrice() {
        int index = batchCombo.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < availableBatches.size()) {
            Batch batch = availableBatches.get(index);
            batchPriceLabel.setText("Цена: " + batch.getSalePrice() + " руб");
        }
    }

    private void addToCart() {
        int index = batchCombo.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showAlert("Ошибка", "Выберите товар");
            return;
        }

        Batch batch = availableBatches.get(index);
        int quantity = quantitySpinner.getValue();

        if (quantity > batch.getQuantity()) {
            showAlert("Ошибка", "Недостаточно товара. Остаток: " + batch.getQuantity());
            return;
        }

        cartList.add(new CartItem(batch.getBatchId(), batch.getMedicineName(), quantity, batch.getSalePrice()));
        updateTotal();
        batchCombo.getSelectionModel().clearSelection();
        quantitySpinner.getValueFactory().setValue(1);
        batchPriceLabel.setText("Цена: 0.00 руб");
    }

    private void updateTotal() {
        double total = cartList.stream().mapToDouble(CartItem::getTotal).sum();
        totalLabel.setText("Итого: " + String.format("%.2f", total) + " руб");
    }

    private void completeSale() {
        if (cartList.isEmpty()) {
            showAlert("Ошибка", "Корзина пуста");
            return;
        }
        if (customerCombo.getValue() == null || employeeCombo.getValue() == null) {
            showAlert("Ошибка", "Выберите клиента и сотрудника");
            return;
        }

        try {
            int employeeId = DatabaseService.getEmployeeId(employeeCombo.getValue());
            int customerId = DatabaseService.getCustomerId(customerCombo.getValue());

            int saleId = DatabaseService.createSale(employeeId, customerId);
            if (saleId == -1) {
                showAlert("Ошибка", "Не удалось создать продажу");
                return;
            }

            for (CartItem item : cartList) {
                DatabaseService.addSaleItem(saleId, item.getBatchId(), item.getQuantity(), item.getPrice());
                Batch batch = availableBatches.stream().filter(b -> b.getBatchId() == item.getBatchId()).findFirst().orElse(null);
                if (batch != null) {
                    DatabaseService.updateBatchQuantity(item.getBatchId(), batch.getQuantity() - item.getQuantity());
                }
            }

            cartList.clear();
            updateTotal();
            loadBatches();
            loadStock();
            loadCustomersAndEmployees();
            statusLabel.setText("Продажа оформлена! Номер чека: " + saleId);
            showAlert("Успех", "Продажа оформлена! Номер чека: " + saleId);

        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось оформить продажу: " + e.getMessage());
        }
    }

    // ==================== ВКЛАДКА: ОТЧЁТ ====================

    private void createReportTab() {
        Tab tab = new Tab("Отчёт по продажам");

        DatePicker startDate = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker endDate = new DatePicker(LocalDate.now());
        Button btnReport = new Button("Показать продажи");

        TableView<SaleReportItem> reportTable = new TableView<>();

        TableColumn<SaleReportItem, Integer> saleIdCol = new TableColumn<>("№ чека");
        saleIdCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));

        TableColumn<SaleReportItem, LocalDate> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        // НОВЫЕ КОЛОНКИ
        TableColumn<SaleReportItem, String> employeeCol = new TableColumn<>("Продавец");
        employeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<SaleReportItem, String> customerCol = new TableColumn<>("Покупатель");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<SaleReportItem, String> medicineCol = new TableColumn<>("Лекарство");
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));

        TableColumn<SaleReportItem, Integer> qtyCol = new TableColumn<>("Кол-во");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SaleReportItem, Double> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SaleReportItem, Double> totalCol = new TableColumn<>("Сумма");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Добавляем ВСЕ колонки (включая новые)
        reportTable.getColumns().addAll(saleIdCol, dateCol, employeeCol, customerCol,
                medicineCol, qtyCol, priceCol, totalCol);

        Label totalSumLabel = new Label("Общая сумма: 0.00 руб");

        btnReport.setOnAction(e -> {
            ObservableList<SaleReportItem> reportList = FXCollections.observableArrayList();
            double totalSum = 0;

            try {
                for (SaleReportItem item : DatabaseService.getSalesReport(startDate.getValue(), endDate.getValue())) {
                    reportList.add(item);
                    totalSum += item.getTotal();
                }
            } catch (SQLException ex) {
                showAlert("Ошибка", ex.getMessage());
            }

            reportTable.setItems(reportList);
            totalSumLabel.setText("Общая сумма: " + String.format("%.2f", totalSum) + " руб");
        });

        HBox topPanel = new HBox(10, new Label("С даты:"), startDate, new Label("По дату:"), endDate, btnReport);
        topPanel.setPadding(new Insets(10));

        VBox vbox = new VBox(10, topPanel, reportTable, totalSumLabel);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        tabPane.getTabs().add(tab);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}