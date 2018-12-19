package models;

import javafx.scene.layout.Pane;

public class ViewController {

	private Pane pane;
	private Object controller;
	
	public ViewController(Pane pane, Object controller) {
		this.pane = pane;
		this.controller = controller;
	}

	public Pane getPane() {
		return pane;
	}

	public Object getController() {
		return controller;
	}
	
}
