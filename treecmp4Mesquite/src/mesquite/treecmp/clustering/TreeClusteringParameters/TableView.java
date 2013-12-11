package mesquite.treecmp.clustering.TreeClusteringParameters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;

import mesquite.lib.ColorDistribution;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteMessage;
import mesquite.lib.MesquiteTool;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Notification;
import mesquite.lib.StringUtil;
import mesquite.lib.table.MesquiteTable;

public class TableView extends MesquiteTable {
	private static final long serialVersionUID = 1L;
	
	private final Table table;
	private final MesquiteWindow window;
	
	public TableView (Table table, int totalWidth, int totalHeight, int columnNamesWidth, MesquiteWindow window) {
		super(table.numRowsTotal, table.numColumnsTotal, totalWidth, totalHeight, columnNamesWidth, window.getColorScheme(), true,false);
		this.table = table;
		this.window = window;
		showRowGrabbers=true;
		showColumnGrabbers=true;
		cornerIsHeading = true;
		setEditable(false, false, false, false);
		setSelectable(true, true, true, true, true, false);
		setUserMove(false, false);
	}
	
	/* ............................................................................................................... */
	/** Selects row */
	public void selectRow(int row) {
		super.selectRow(row);
		if (false && rowLegal(row)){
			if (getRowAssociable() != null) {
				getRowAssociable().setSelected(row, true);
				//getRowAssociable().notifyListeners(this, new Notification(MesquiteListener.SELECTION_CHANGED));
			}
		}
	}

	/* ............................................................................................................... */
	/** Selects rows */
	public void selectRows(int first, int last) {
		super.selectRows(first, last);
		if (false && rowLegal(first) && rowLegal(last)) {
			int r1 = MesquiteInteger.minimum(first, last);
			int r2 = MesquiteInteger.maximum(first, last);
			for (int i = r1; i <= r2; i++) {
				if (getRowAssociable() != null) {
					getRowAssociable().setSelected(i, true);
				}
			}
		}
		if (false && getRowAssociable() != null) 
			getRowAssociable().notifyListeners(this, new Notification(MesquiteListener.SELECTION_CHANGED));

	}

	/* ............................................................................................................... */
	/** repaints all components of the table */
	public void repaintAll() {
		// checkResetFont(getGraphics());
		columnNames.repaint();
		repaint();
		rowNames.repaint();
		cornerCell.repaint();
		matrix.repaint();
	}
	/*...............................................................................................................*/
	/** Gets whether the cell is dimmed (true) or not (false).  Column -1 is for row names; row -1 is for column names.*/
	public boolean getCellDimmed(int column, int row){
		if (column == -1 && (row == -1))
				return getNumRows()<=0;
		return super.getCellDimmed(column, row);
	}
	/*...............................................................................................................*/
	/** returns whether or not a cells of table editable by default.*/
	public boolean cellsEditableByDefault(){
		return false;
	}
	/*...............................................................................................................*/
	/** returns whether or not a row name of table is editable.*/
	public boolean checkRowNameEditable(int row){
		return super.isRowNameEditable(row);
	}
	/*...............................................................................................................*/
	/*...............................................................................................................*/
	/** returns whether or not a column name of table is editable.*/
	public boolean checkColumnNameEditable(int column){
		return super.isColumnNameEditable(column);
	}
	public boolean useString(int column, int row) {
		return true;
	}
	public String getMatrixText(int column, int row){  
		final Column columnModel = table.columns.get(column);
		final Row rowData = table.rows[row];
		return columnModel.getString(rowData);
	}
	public void drawMatrixCell(Graphics g, int x, int y,  int w, int h, int column, int row, boolean selected){
		final String s = getMatrixText(column, row);
		
		FontMetrics fm = getFontMetrics(getFont());
		int sw = fm.stringWidth(s);
		int sh = fm.getMaxAscent()+ fm.getMaxDescent();
		try{
			Color old = g.getColor();
			Color c = getBackground();
			if (c!=null)
				if (selected)
					c = Color.black;
				else if (isCellEditable(column, row))
					c = Color.cyan;
				else
					c = ColorDistribution.uneditable;
			if (c!=null) g.setColor(c);
			g.fillRect(x, y, w, h);
			if (selected)
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawString(s, x+(w-sw)/2, y+h-(h-sh)/2);
			g.setColor(old);
			
		}
		catch (NullPointerException e){
			MesquiteMessage.printStackTrace("NPE");
		}
	}
	
	public void drawRowNameCell(Graphics g, int x, int y,  int w, int h, int row){
		final Row rowData = table.rows[row];
		String s = rowData.name;
		if (s == null)
			return;
		Shape clip = g.getClip();
		g.setClip(x,y,w,h);
		if (isRowNameSelectedAnyWay(row)){
			Color c = g.getColor();
			g.setColor(ColorDistribution.straw);
			g.fillRect(x,y,w,h);
			if (c!=null) g.setColor(c);
		}

		int gnso = x+getNameStartOffset();
		g.drawString(s, gnso, y+h-4);
		g.setClip(clip);
	}
	/*...............................................................................................................*/
	public void mouseInCell(int modifiers, int column,int subColumn, int row, int subRow, MesquiteTool tool){
		if (row == -1 && column>=0){ //column names
			final Column columnModel = table.columns.get(column);
			String ex = columnModel.title + "\n" + columnModel.explanation;
			window.setExplanation(ex);
		} else if (row >= 0 && column == -1) {	//row names
			final Row rowData = table.rows[row];
			String ex = rowData.name + "\n" + rowData.explanation;
			window.setExplanation(ex);
		} else if (row >=0 && column>=0){ //internal cell
			final Row rowData = table.rows[row];
			String ex = rowData.explanation;
			if (StringUtil.blank(ex)) {
				final Column columnModel = table.columns.get(column);
				ex = columnModel.title;
			}
			window.setExplanation(ex);
		}
	}
	/*...............................................................................................................*/
	public void mouseExitedCell(int modifiers, int column,int subColumn,int row, int subRow,MesquiteTool tool){
		if (row == -1 && column>=0){//column names
			window.setExplanation("");
		}
	}
	/*...............................................................................................................*/
	/** Returns text in row name.  */
	public String getColumnNameText(int column){
		final Column columnModel = table.columns.get(column);
		return columnModel.title;
	}
	/*...............................................................................................................*/
	/** Returns text in row name.  */
	public String getRowNameText(int row){
		final Row rowData = table.rows[row];
		return rowData.name;
	}

	@Override
	public void cellTouched(int column, int row, int regionInCellH,
			int regionInCellV, int modifiers, int clickCount) {
		super.cellTouched(column, row, regionInCellH, regionInCellV, modifiers,
				clickCount);
		tableTouchedEvent.fire();
	}

	@Override
	public void columnTouched(boolean isArrowEquivalent, int column,
			int regionInCellH, int regionInCellV, int modifiers) {
		super.columnTouched(isArrowEquivalent, column, regionInCellH, regionInCellV,
				modifiers);
		tableTouchedEvent.fire();
	}

	@Override
	public void rowTouched(boolean asArrow, int row, int regionInCellH,
			int regionInCellV, int modifiers) {
		super.rowTouched(asArrow, row, regionInCellH, regionInCellV, modifiers);
		tableTouchedEvent.fire();
	}
	
	public void onTableTouched(Runnable listener) {
		tableTouchedEvent.subscribe(listener);
	}
	
	private final Event tableTouchedEvent = new Event();
}