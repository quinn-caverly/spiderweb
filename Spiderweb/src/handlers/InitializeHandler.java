package handlers;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;

public class InitializeHandler {
	
    private Integer heightOfTitleArea = 10;
    private Integer heightOfSeparator = 8;
    private final Double goldenRatio = (double) (1+Math.sqrt(5))/2;


	///template for all of the handlers which follow, in order to make initialize statements in MainClassController 
    ///more transparent
    
	public class Template {
		
		public Template() {}
		
		//for pref, max, min, width unless has setSelfRatio
		public void setWidths() {}
		
		//for pref, max, min, height unless has setSelfRatio
		public void setHeights() {}
		
		//for managing proportions of object if they must be kept
		//EX: height must be twice width
		public void setSelfRatio() {}
		
		//manages the position in its parent
		public void setLocation() {}
				
	}
	
	public class BottomBarHandler extends Template {
		
		HBox self;
		
		public BottomBarHandler(HBox bottomBar) {
			this.self = bottomBar;
		}

		@Override
		public void setHeights() {
			self.maxHeight(50);
			self.minHeight(50);
		}
		
		
		public class LeftLabelHandler extends Template {
			
			Label self;
			
			public LeftLabelHandler(Label label) {
				this.self = label;
			}

			@Override
			public void setHeights() {
				// TODO Auto-generated method stub
				super.setHeights();
			}

			@Override
			public void setLocation() {
				// TODO Auto-generated method stub
				super.setLocation();
			}
		}
		
		
		public class RightLabelHandler extends Template {
			
			Label self;
			
			public RightLabelHandler(Label label) {
				this.self = label;
			}

			@Override
			public void setHeights() {
				// TODO Auto-generated method stub
				super.setHeights();
			}

			@Override
			public void setLocation() {
				// TODO Auto-generated method stub
				super.setLocation();
			}
		}
		
		
		
	}
		
	public class ImagePoolHandler extends Template {
		
		AnchorPane self;
		AnchorPane marginKeeper;
		
		public ImagePoolHandler(AnchorPane imagePool) {
			this.self = imagePool;
			this.marginKeeper = (AnchorPane) imagePool.getParent();
		}

		@Override
		public void setWidths() {
			self.prefWidthProperty().bind(marginKeeper.widthProperty());
		}

		@Override
		public void setHeights() {
			self.prefHeightProperty().bind(marginKeeper.heightProperty());
		}

		@Override
		public void setSelfRatio() {
			self.maxWidthProperty().bind(marginKeeper.heightProperty().divide(goldenRatio));
			self.maxHeightProperty().bind(marginKeeper.widthProperty().multiply(goldenRatio));
		}

		@Override
		public void setLocation() {
			//centers the imagePool by taking (width of parent - width of child) / 2
			self.translateXProperty()
			.bind(((AnchorPane) self.getParent()).widthProperty().subtract(self.widthProperty())
					.divide(2));
	    
			self.translateYProperty()
			.bind(((AnchorPane) self.getParent()).heightProperty().subtract(self.heightProperty())
					.divide(2));	
		}	
	}
	
	public class ImagePoolLabelHandler extends Template {
		
		Label self;
		AnchorPane imagePool;
		
		public ImagePoolLabelHandler(Label label) {
			this.self = label;
			this.imagePool = (AnchorPane) label.getParent();
		}
		
		@Override
		public void setLocation() {
			//centers the imagePool by taking (width of parent - width of child) / 2
			
			self.setTranslateX(imagePool.getWidth()/2);
			self.setTranslateY(imagePool.getHeight()/2);
			
			self.translateXProperty().bind(imagePool.widthProperty().multiply(1/2));
			self.translateYProperty().bind(imagePool.heightProperty().multiply(1/2));
		}	
		
		
	}
	
	
	public class TextPaneHandler extends Template {
		
		Pane self;
		Pane pane;
		AnchorPane parent;
		
		public TextPaneHandler(Pane pane) {
			this.self = pane;
			this.pane = pane;
			this.parent = (AnchorPane) pane.getParent();
		}
		
		
		@Override
		public void setWidths() {
			self.setMinWidth(50);
		}

		@Override
		public void setHeights() {
			self.setMinHeight(50);
			self.prefHeightProperty().bind(parent.heightProperty());
		}
		
		@Override
		public void setLocation() {
			//centers the text section by taking (width of parent - width of child) / 2
			self.translateXProperty()
			.bind(parent.widthProperty().subtract(self.widthProperty())
					.divide(2));
	    
			self.translateYProperty()
			.bind(parent.heightProperty().subtract(self.heightProperty())
					.divide(2));
		}
		
		@Override
		public void setSelfRatio() {
			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			
			self.setMaxHeight(screenBounds.getHeight()/goldenRatio);			
			self.maxWidthProperty().bind(self.heightProperty().divide(goldenRatio));
			
			self.prefWidthProperty().bind(self.heightProperty().divide(goldenRatio));

		}
		
		
		
		

		public class TitleAreaHandler extends InitializeHandler.Template {
			
			TextArea self;
			Pane parent;
			
			public TitleAreaHandler(TextArea titleArea) {
				this.self = titleArea;
				this.parent = (Pane) titleArea.getParent();
				
				self.setPrefRowCount(1);
				
				self.setWrapText(true);
			}

			@Override
			public void setWidths() {
				//bound to parent
				self.minWidthProperty().bind(parent.widthProperty());
				self.maxWidthProperty().bind(parent.widthProperty());
			}

			@Override
			public void setHeights() {
				//Title area's height is initialized to 20
				self.maxHeight(heightOfTitleArea);
				self.minHeight(heightOfTitleArea);	
			}
		}
		
		public class MainTextAreaHandler extends InitializeHandler.Template {
			
			TextArea self;
			Pane parent;
			AnchorPane separator;
			TextArea titleArea;
			
			public MainTextAreaHandler(TextArea mainTextArea, AnchorPane separator, TextArea titleArea) {
				this.self = mainTextArea;
				this.titleArea = titleArea;
				this.separator = separator;
				this.parent = (Pane) mainTextArea.getParent();
				
				self.setWrapText(true);
			}

			@Override
			public void setWidths() {
				//bound to parent
				self.minWidthProperty().bind(parent.widthProperty());
				self.maxWidthProperty().bind(parent.widthProperty());
			}
		}
		
		public class SeperatorHandler extends InitializeHandler.Template {
			
			AnchorPane self;
			Pane parent;
			TextArea titleArea;
			
			public SeperatorHandler(AnchorPane separator, TextArea titleArea) {
				this.self = separator;
				this.titleArea = titleArea;
				this.parent = (Pane) separator.getParent();
			}

			@Override
			public void setWidths() {
				//bound to parent
				self.minWidthProperty().bind(parent.widthProperty());
				self.maxWidthProperty().bind(parent.widthProperty());
			}

			@Override
			public void setHeights() {
				self.maxHeight(heightOfSeparator);
				self.minHeight(heightOfSeparator);
			}
		}
	}


}
