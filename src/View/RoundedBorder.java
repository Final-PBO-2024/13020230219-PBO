package View; // Atau package utilitas Anda

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;

class RoundedBorder extends AbstractBorder {
    private Color color;
    private int arc;
    private int thickness;

    public RoundedBorder(Color color, int arc, int thickness) {
        this.color = color;
        this.arc = arc;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(this.color);
        g2d.setStroke(new BasicStroke(this.thickness));
        // Menggambar bentuk rounded rectangle untuk border
        // Disesuaikan agar border tidak terlalu masuk ke dalam komponen
        g2d.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0, 
                                            width - thickness, height - thickness, 
                                            arc, arc));
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.thickness + 4, this.thickness + 8, this.thickness + 4, this.thickness + 8); // Memberi padding
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = this.thickness + 8;
        insets.top = insets.bottom = this.thickness + 4;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true; 
    }
}