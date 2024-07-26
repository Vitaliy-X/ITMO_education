#include "gradientpicker.h"
#include <QPainter>
#include <QMouseEvent>

GradientPicker::GradientPicker(QWidget *parent)
    : QWidget(parent)
{
    color1 = QColor(255, 0, 0); // Default color 1 (red)
    color2 = QColor(0, 0, 255); // Default color 2 (blue)
}

void GradientPicker::paintEvent(QPaintEvent *event)
{
    QPainter painter(this);
    painter.setRenderHint(QPainter::Antialiasing);

    // Draw the gradient rectangle
    QRect gradientRect(10, 10, width() - 20, height() - 20);
    QLinearGradient gradient(gradientRect.topLeft(), gradientRect.bottomRight());
    gradient.setColorAt(0, color1);
    gradient.setColorAt(1, color2);
    painter.fillRect(gradientRect, gradient);

    // Draw the color indicators
    painter.setPen(Qt::black);
    painter.setBrush(color1);
    painter.drawEllipse(gradientRect.topLeft(), 10, 10);
    painter.setBrush(color2);
    painter.drawEllipse(gradientRect.bottomRight(), 10, 10);
}

void GradientPicker::mousePressEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton) {
        // Check if the mouse press is inside the gradient rectangle
        QRect gradientRect(10, 10, width() - 20, height() - 20);
        if (gradientRect.contains(event->pos())) {
            // Calculate the color position within the gradient rectangle
            qreal position = (qreal)(event->pos().x() - gradientRect.left()) / gradientRect.width();

            // Update the color based on the position
            if (position <= 0)
                color1 = color2;
            else if (position >= 1)
                color2 = color1;
            else if (qAbs(color1.hue() - color2.hue()) > 180) {
                if (color1.hue() < color2.hue())
                    color1 = QColor::fromHslF(position * 360, color1.saturationF(), color1.lightnessF());
                else
                    color2 = QColor::fromHslF(position * 360, color2.saturationF(), color2.lightnessF());
            } else {
                if (color1.hue() < color2.hue())
                    color2 = QColor::fromHslF(position * 360, color2.saturationF(), color2.lightnessF());
                else
                    color1 = QColor::fromHslF(position * 360, color1.saturationF(), color1.lightnessF());
            }

            update();

            // Emit the gradientChanged signal with the updated colors
            emit gradientChanged(color1, color2);
        }
    }
}

void GradientPicker::mouseMoveEvent(QMouseEvent *event)
{
    if (event->buttons() & Qt::LeftButton) {
        // Call the same logic as in mousePressEvent to update the colors
        mousePressEvent(event);
    }
}
