package mesquite.treecomp.common;

import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class AwtUtils {
	public static Label HyperlinkBehaviour(Label component) {
		return HyperlinkBehaviour(component, null);
	}
	
	public static Label HyperlinkBehaviour(final Label component, final ActionListener actionListener) {
		component.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
			}
			
			public void mouseExited(MouseEvent e) {
				Font f = component.getFont();
				Font newFont = f.deriveFont(f.getStyle() & ~Font.BOLD);
				component.setFont(newFont);
			}
			
			public void mouseEntered(MouseEvent e) {
				Font f = component.getFont();
				Font newFont = f.deriveFont(f.getStyle() | Font.BOLD);
				component.setFont(newFont);
			}
			
			public void mouseClicked(MouseEvent e) {
				if (actionListener != null)
					actionListener.actionPerformed(
							new ActionEvent(component, 1, null));
			}
		});
		
		return component;
	}
	
	public static TextField NumericTextFieldBehaviour(TextField field) {
		field.addKeyListener(new KeyListener() {			
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() < '0' || e.getKeyChar() > '9')
						&& e.getKeyChar() != KeyEvent.VK_BACK_SPACE)
					e.consume();
				return;
			}
			
			public void keyReleased(KeyEvent e) {
				return;
			}
			
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() < '0' || e.getKeyCode() > '9')
					e.consume();
				return;
			}
		});
		return field;
	}
}