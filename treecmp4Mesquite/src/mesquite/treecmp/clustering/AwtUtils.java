package mesquite.treecmp.clustering;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class AwtUtils {
	private AwtUtils() {}
	
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

	public static void fillComponent(Container container,
			Component component) {
		container.setLayout(new BorderLayout());
		container.addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {
			}
			
			public void componentResized(ComponentEvent e) {
				e.getComponent().revalidate();
			}
			
			public void componentMoved(ComponentEvent e) {				
			}
			
			public void componentHidden(ComponentEvent e) {				
			}
		});
		container.add(component);
	}
}