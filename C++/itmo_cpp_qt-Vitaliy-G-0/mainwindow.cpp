#include "mainwindow.h"
#include "ButtonStylesheet.h"
#include <QFileDialog>
#include <QGroupBox>
#include <QVBoxLayout>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    plot = new Plot(this);
    saveButton = new QPushButton("Save", this);
    loadButton = new QPushButton("Load", this);

    functionGroup = new QButtonGroup(this);
    function1Button = new QRadioButton("Sinc1", this);
    function2Button = new QRadioButton("Sinc2", this);

    gradientGroup = new QButtonGroup(this);
    gradient1Button = new QRadioButton("Gradient 1", this);
    gradient2Button = new QRadioButton("Gradient 2", this);

    functionGroup->addButton(function1Button, 0);
    functionGroup->addButton(function2Button, 1);
    function1Button->setChecked(true);

    gradientGroup->addButton(gradient1Button, 0);
    gradientGroup->addButton(gradient2Button, 1);
    gradient1Button->setChecked(true);

    connect(functionGroup, SIGNAL(buttonClicked(int)), this, SLOT(functionSelected(int)));
    connect(gradientGroup, SIGNAL(buttonClicked(int)), this, SLOT(gradientSelected(int)));
    connect(this, SIGNAL(functionSelectionChanged(int)), plot, SLOT(updateSurface(int)));
    connect(this, SIGNAL(gradientSelectionChanged(int)), plot, SLOT(updateGradient(int)));

    // Sliders
    sliderStep = new QSlider(Qt::Horizontal);
    sliderStep->setMinimum(1);
    sliderStep->setMaximum(100);
    sliderStep->setValue(50);

    stepLabel = new QLabel();
    stepLabel->setText(QString::number(sliderStep->value()));
    connect(sliderStep, &QSlider::valueChanged, this, &MainWindow::updateSliderLabel);

    rangeMinSpinBox = new QSpinBox(this);
    rangeMinSpinBox->setMinimum(-100);
    rangeMinSpinBox->setMaximum(100);
    rangeMinSpinBox->setValue(-10);

    rangeMaxSpinBox = new QSpinBox(this);
    rangeMaxSpinBox->setMinimum(-100);
    rangeMaxSpinBox->setMaximum(100);
    rangeMaxSpinBox->setValue(10);

    connect(sliderStep, &QSlider::valueChanged, this, &MainWindow::onSliderValueChanged);
    connect(this, &MainWindow::stepSelectionChanged, plot, &Plot::updateStep);
    connect(rangeMinSpinBox, QOverload<int>::of(&QSpinBox::valueChanged), this, &MainWindow::onRangeMinChanged);
    connect(rangeMaxSpinBox, QOverload<int>::of(&QSpinBox::valueChanged), this, &MainWindow::onRangeMaxChanged);

    // Display Options
    gridCheckBox = new QCheckBox("Show grid", this);
    labelCheckBox = new QCheckBox("Show label", this);
    labelBorderCheckBox = new QCheckBox("Show label border", this);
    labelBorderCheckBox->setEnabled(true);

    gridCheckBox->setChecked(true);
    labelCheckBox->setChecked(true);
    labelBorderCheckBox->setChecked(true);

    connect(gridCheckBox, SIGNAL(stateChanged(int)), this, SLOT(gridCheckBoxChanged(int)));
    connect(labelCheckBox, SIGNAL(stateChanged(int)), this, SLOT(labelCheckBoxChanged(int)));
    connect(labelBorderCheckBox, SIGNAL(stateChanged(int)), this, SLOT(labelBorderCheckBoxChanged(int)));

    QVBoxLayout *checkBoxLayout = new QVBoxLayout;
    checkBoxLayout->addWidget(gridCheckBox);
    checkBoxLayout->addWidget(labelCheckBox);
    checkBoxLayout->addWidget(labelBorderCheckBox);

    QGroupBox *checkBoxGroupBox = new QGroupBox("Display Options", this);
    checkBoxGroupBox->setLayout(checkBoxLayout);

    // Create the label for the spin box range
    QLabel *rangeLabel = new QLabel("Range:", this);

    // Create a horizontal layout for the label and spin boxes
    QHBoxLayout *rangeLayout = new QHBoxLayout;
    rangeLayout->addWidget(rangeLabel);
    rangeLayout->addWidget(rangeMinSpinBox);
    rangeLayout->addWidget(rangeMaxSpinBox);

    // Layout
    QVBoxLayout *radioButtonLayout = new QVBoxLayout;
    radioButtonLayout->addWidget(function1Button);
    radioButtonLayout->addWidget(function2Button);

    QVBoxLayout *gradientLayout = new QVBoxLayout;
    gradientLayout->addWidget(gradient1Button);
    gradientLayout->addWidget(gradient2Button);

    QVBoxLayout *sliderLayout = new QVBoxLayout;
    sliderLayout->addWidget(sliderStep);
    sliderLayout->addWidget(stepLabel);

    QGroupBox *radioButtonGroupBox = new QGroupBox("Plot", this);
    radioButtonGroupBox->setLayout(radioButtonLayout);

    QGroupBox *gradientGroupBox = new QGroupBox("Gradient", this);
    gradientGroupBox->setLayout(gradientLayout);

    QGroupBox *sliderGroupBox = new QGroupBox("Step", this);
    sliderGroupBox->setLayout(sliderLayout);

    QVBoxLayout *settingsLayout = new QVBoxLayout;
    settingsLayout->addWidget(saveButton);
    settingsLayout->addWidget(loadButton);
    settingsLayout->addWidget(radioButtonGroupBox);
    settingsLayout->addWidget(gradientGroupBox);
    settingsLayout->addWidget(checkBoxGroupBox);
    settingsLayout->addWidget(sliderGroupBox);
    settingsLayout->addLayout(rangeLayout);
    settingsLayout->addStretch();

    QGroupBox *settingsContainer = new QGroupBox("Settings", this);
    settingsContainer->setFixedWidth(220);
    settingsContainer->setLayout(settingsLayout);

    QHBoxLayout *mainLayout = new QHBoxLayout;
    mainLayout->addWidget(plot);
    mainLayout->addWidget(settingsContainer);

    QWidget *centralWidget = new QWidget(this);
    centralWidget->setLayout(mainLayout);
    setCentralWidget(centralWidget);

    // Connect save and load buttons
    connect(saveButton, SIGNAL(clicked()), this, SLOT(saveSettings()));
    connect(loadButton, SIGNAL(clicked()), this, SLOT(loadSettings()));

    // Apply custom styles to radio buttons
    function1Button->setStyleSheet(ButtonStylesheet);
    function2Button->setStyleSheet(ButtonStylesheet);
    gradient1Button->setStyleSheet(ButtonStylesheet);
    gradient2Button->setStyleSheet(ButtonStylesheet);
    sliderStep->setStyleSheet(SliderStylesheet);
    rangeMinSpinBox->setStyleSheet(SpinBoxStylesheet);
    rangeMaxSpinBox->setStyleSheet(SpinBoxStylesheet);
    gridCheckBox->setStyleSheet(CheckBoxStylesheet);
    labelCheckBox->setStyleSheet(CheckBoxStylesheet);
    labelBorderCheckBox->setStyleSheet(CheckBoxStylesheet);

    loadSettings();
}

void MainWindow::functionSelected(int id)
{
    emit functionSelectionChanged(id);

    if (plot->getCurrentGradient() != gradientGroup->checkedId())
    {
        switch (plot->getCurrentGradient())
        {
            case 0:
                gradient1Button->setChecked(1);
                break;
            case 1:
                gradient2Button->setChecked(1);
                break;
        }
    }
}

void MainWindow::gradientSelected(int id)
{
    emit gradientSelectionChanged(id);
}

void MainWindow::gridCheckBoxChanged(int state)
{
    plot->setDisplayGrid(state == Qt::Checked);
}

void MainWindow::labelCheckBoxChanged(int state)
{
    bool displayLabels = (state == Qt::Checked);
    plot->setDisplayLabels(displayLabels);
    labelBorderCheckBox->setEnabled(displayLabels);

    if (!displayLabels)
    {
        plot->setDisplayLabelBorders(false);
        labelBorderCheckBox->setChecked(false);
    }
}

void MainWindow::labelBorderCheckBoxChanged(int state)
{
    plot->setDisplayLabelBorders(state == Qt::Checked);
}

void MainWindow::saveSettings()
{
    QSettings settings("settings.ini", QSettings::IniFormat);

    // Save settings for function 1
    settings.beginGroup("Function1");
    settings.setValue("gradientId", gradientGroup->checkedId());
    settings.setValue("rangeMin", rangeMinSpinBox->value());
    settings.setValue("rangeMax", rangeMaxSpinBox->value());
    settings.setValue("step", sliderStep->value());
    settings.endGroup();

    // Save settings for function 2
    settings.beginGroup("Function2");
    settings.setValue("gradientId", gradientGroup->checkedId());
    settings.setValue("rangeMin", rangeMinSpinBox->value());
    settings.setValue("rangeMax", rangeMaxSpinBox->value());
    settings.setValue("step", sliderStep->value());
    settings.endGroup();

    settings.setValue("gridDisplay", gridCheckBox->isChecked());
    settings.setValue("labelDisplay", labelCheckBox->isChecked());
    settings.setValue("labelBorderDisplay", labelBorderCheckBox->isChecked());
}

void MainWindow::loadSettings()
{
    QSettings settings("settings.ini", QSettings::IniFormat);

    // Load settings for function 1
    settings.beginGroup("Function1");
    int gradientIdFunction1 = settings.value("gradientId", 0).toInt();
    int rangeMinFunction1 = settings.value("rangeMin", -10).toInt();
    int rangeMaxFunction1 = settings.value("rangeMax", 10).toInt();
    int stepFunction1 = settings.value("step", 50).toInt();
    settings.endGroup();
    emit gradientSelectionChanged(gradientIdFunction1);
    rangeMinSpinBox->setValue(rangeMinFunction1);
    rangeMaxSpinBox->setValue(rangeMaxFunction1);
    sliderStep->setValue(stepFunction1);

    // Load settings for function 2
    settings.beginGroup("Function2");
    int gradientIdFunction2 = settings.value("gradientId", 0).toInt();
    int rangeMinFunction2 = settings.value("rangeMin", -10).toInt();
    int rangeMaxFunction2 = settings.value("rangeMax", 10).toInt();
    int stepFunction2 = settings.value("step", 50).toInt();
    settings.endGroup();
    emit gradientSelectionChanged(gradientIdFunction2);
    rangeMinSpinBox->setValue(rangeMinFunction2);
    rangeMaxSpinBox->setValue(rangeMaxFunction2);
    sliderStep->setValue(stepFunction2);

    gridCheckBox->setChecked(settings.value("gridDisplay", true).toBool());
    labelCheckBox->setChecked(settings.value("labelDisplay", true).toBool());
    labelBorderCheckBox->setChecked(settings.value("labelBorderDisplay", true).toBool());

    // Update UI with loaded settings for function1
    gradientGroup->button(gradientIdFunction1)->setChecked(true);

    // Update UI with loaded settings for function 2
    gradientGroup->button(gradientIdFunction2)->setChecked(true);

    gridCheckBoxChanged(gridCheckBox->checkState());
    labelCheckBoxChanged(labelCheckBox->checkState());
    labelBorderCheckBoxChanged(labelBorderCheckBox->checkState());
}

void MainWindow::onSliderValueChanged(int value)
{
    emit stepSelectionChanged(value);
}

void MainWindow::onRangeMinChanged(int value)
{
    plot->setRangeMin(value);
}

void MainWindow::onRangeMaxChanged(int value)
{
    plot->setRangeMax(value);
}

void MainWindow::updateSliderLabel(int value)
{
    stepLabel->setText(QString::number(value));
}
