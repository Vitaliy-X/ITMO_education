// gradientpicker.h

#ifndef GRADIENTPICKER_H
#define GRADIENTPICKER_H

#include <QWidget>
#include <QColor>

class GradientPicker : public QWidget
{
    Q_OBJECT

public:
    explicit GradientPicker(QWidget *parent = nullptr);

signals:
    void gradientChanged(const QColor &color1, const QColor &color2);

private:
    QColor color1;
    QColor color2;

    void paintEvent(QPaintEvent *event);
    void mousePressEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);
};

#endif // GRADIENTPICKER_H
