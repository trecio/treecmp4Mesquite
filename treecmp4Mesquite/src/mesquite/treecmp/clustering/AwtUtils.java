package mesquite.treecmp.clustering;

import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class AwtUtils {
	public static Label hyperlinkBehaviour(Label component) {
		return hyperlinkBehaviour(component, null);
	}
	
	public static Label hyperlinkBehaviour(final Label component, final ActionListener actionListener) {
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
	
	public static TextField numericTextFieldBehaviour(TextField field) {
		field.addKeyListener(new KeyListener() {			
			public void keyTyped(KeyEvent e) {
				if (!passesFilter(e.getKeyChar()))
					e.consume();
				return;
			}
			
			private boolean passesFilter(char keyCode) {
				return ('0' <= keyCode && keyCode <= '9')
					|| keyCode == KeyEvent.VK_BACK_SPACE;
			}

			public void keyReleased(KeyEvent e) {
				return;
			}
			
			public void keyPressed(KeyEvent e) {
				if (!passesFilter(e.getKeyChar()))
					e.consume();
				return;
			}
		});
		return field;
	}
}