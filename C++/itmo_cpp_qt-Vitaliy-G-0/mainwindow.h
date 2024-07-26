#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include "plot.h"
#include <QMainWindow>
#include <QPushButton>
#include <QRadioButton>
#include <QButtonGroup>
#include <QCheckBox>
#include <QSlider>
#include <QLabel>
#include <QSpinBox>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);

private slots:
    void functionSelected(int id);
    void gradientSelected(int id);
    void onSliderValueChanged(int value);
    void onRangeMinChanged(int value);
    void onRangeMaxChanged(int value);
    void gridCheckBoxChanged(int state);
    void labelCheckBoxChanged(int state);
    void labelBorderCheckBoxChanged(int state);
    void saveSettings();
    void loadSettings();
    void updateSliderLabel(int value);

signals:
    void functionSelectionChanged(int function);
    void gradientSelectionChanged(int gradient);
    void stepSelectionChanged(int step);
    void rangeSelectionChanged(double rangeMin, double rangeMax);

private:
    Plot *plot;
    QPushButton *saveButton;
    QPushButton *loadButton;
    QButtonGroup *functionGroup;
    QRadioButton *function1Button;
    QRadioButton *function2Button;
    QButtonGroup *gradientGroup;
    QRadioButton *gradient1Button;
    QRadioButton *gradient2Button;
    QCheckBox *gridCheckBox;
    QCheckBox *labelCheckBox;
    QCheckBox *labelBorderCheckBox;
    QSlider *sliderStep;
    QLabel* stepLabel;
    QSpinBox* rangeMinSpinBox;
    QSpinBox* rangeMaxSpinBox;
};

#endif // MAINWINDOW_H
