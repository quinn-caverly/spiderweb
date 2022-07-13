package overriders;

import java.io.Serializable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class FluidImageWrapper extends AnchorPane implements Serializable {
	AnchorPane imagePool;
	public Image img;
	Double borderBuffer = (double) 2;		
	
	Boolean activated = false;
	Boolean pressed = false;
	
    private final Double goldenRatio = (double) (1+Math.sqrt(5))/2;
	
	private FluidImage fluidImage;
	
	public Event event;
		
	public FluidImageWrapper(AnchorPane imagePool, Image img) {
		this.imagePool = imagePool;
		this.img = img;
				
		fluidImage = new FluidImage();
		fluidImage.setImage(img);
		
		this.getChildren().add(fluidImage);
		fluidImage.bindWithWrapper();
		
		
		this.setOnMouseClicked(onClick);

		
		ContextMenu contextMenu = new ContextMenu();
		
        //Open the context menu on the FIW
	    this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent event) {
	             contextMenu.show(FluidImageWrapper.this, event.getScreenX(), event.getScreenY());
			}});
		
		MenuItem delete = new MenuItem("Delete");
		contextMenu.getItems().add(delete);
	    
        //Open Note Menu Item Handler
	    delete.setOnAction(new EventHandler<ActionEvent>() { 
		@Override
		public void handle(ActionEvent event) {
			imagePool.getChildren().remove(FluidImageWrapper.this);
		}});
		
	}
	
	public String createStringForDataFile() {		
		Double imageWidth = fluidImage.getFitWidth();
		Double imageHeight = fluidImage.getFitHeight();
		
		Double anchorWidth = imagePool.getWidth();
		Double anchorHeight = imagePool.getHeight();
		
		Double widthRatio = imageWidth/anchorWidth;
		Double heightRatio = imageHeight/anchorHeight;
		
		Double translateXRatio = this.getTranslateX() / imagePool.getWidth();
		Double translateYRatio = this.getTranslateY() / imagePool.getHeight();
		
		return widthRatio + "\n" + heightRatio + "\n" + translateXRatio + "\n" + translateYRatio;
	}
	
	
	public void placeFromDataFile(String data) {
		
		String[] listOfRatios = data.split("\n");
		
		for (String ratio : listOfRatios) {
			System.out.println(ratio);
		}
		
		Double widthRatio = Double.parseDouble(listOfRatios[0]);
		Double heightRatio = Double.parseDouble(listOfRatios[1]);
		
		Double translateXRatio = Double.parseDouble(listOfRatios[2]);
		Double translateYRatio = Double.parseDouble(listOfRatios[3]);
		

		//makes image resize when imagePool resizes
		this.bindProportionality(widthRatio, heightRatio);
		
		//makes image stay in the same relative position through resizes
		this.bindLayout(translateXRatio, translateYRatio);
		
		imagePool.getChildren().add(this);
	}
	
	
	
	public void initialPlace(DragEvent event) {
		//if too big for the window resizes it
		this.setInitialSize();
		
		//sets the location of the middle of the mouse, if this would be outside the border, pulls it back in
		this.event = event;
		this.setLocMiddleOfCursor(event);
		
		//makes image resize when imagePool resizes
		this.bindProportionality();
		
		//makes image stay in the same relative position through resizes
		this.bindLayout();
	}

	//for the initial placement of the image, changes when the window size changes
	public void setInitialSize() {
		
		double anchorWidth = this.imagePool.getWidth();
		double anchorHeight = this.imagePool.getHeight();
		
		double imageWidth = img.getWidth();
		double imageHeight = img.getHeight();
		
		double heightToReturn = 0;
		double widthToReturn = 0;
		
	    double[] toReturn = new double[2];
		
		//for use in the if statements below
		double heightRatio = imageHeight / anchorHeight;
		double widthRatio = imageWidth / anchorWidth;
		
		if (imageWidth > anchorWidth || imageHeight > anchorHeight) {
			//for if the image is bigger vertically and horizontally...
			
			//whichever ratio is larger will be the constraining aspect
			if (heightRatio > widthRatio) {
				//both are reduced by the same ratio in order to stay proportionate
				heightToReturn = imageHeight/heightRatio;
				widthToReturn = imageWidth/heightRatio;
			}
			
			//if the width has a larger ratio
			else {
				heightToReturn = imageHeight/widthRatio;
				widthToReturn = imageWidth/widthRatio;
				
			}
			
		    toReturn[0] = widthToReturn;
		    toReturn[1] = heightToReturn;
		}
		
		else if (imageWidth > anchorWidth) {
			//width will be the width of anchor
			heightToReturn = imageHeight/widthRatio;
			widthToReturn = imageWidth/widthRatio;
			
		    toReturn[0] = widthToReturn;
		    toReturn[1] = heightToReturn;
		}
		
		else if (imageHeight > anchorHeight) {
			//height will be height of the anchor
			heightToReturn = imageHeight/heightRatio;
			widthToReturn = imageWidth/heightRatio;
			
		    toReturn[0] = widthToReturn;
		    toReturn[1] = heightToReturn;
		}

		//sets values accordingly
		
		if (toReturn[0] == 0.0 || toReturn[1] == 0.0) {
			//if toReturn was never assigned, sets width and height to img width and height
			this.setHeight(img.getHeight());
			this.setWidth(img.getWidth());
				
		}

		else {
			//changes the size of the FluidImage
			this.setWidth(toReturn[0] - borderBuffer* 2);
			this.setHeight(toReturn[1] - borderBuffer *2);
		}
	}
	
	//deals with where the image is initially placed in the imagePool
	public void setLocMiddleOfCursor(Event event) {
		
		
		//adds ability to handle both DragEvent and MouseMoved event
		Double mouseX;
		Double mouseY;
				
		if (event instanceof DragEvent) {
			DragEvent eventCasted = (DragEvent) event;
			
			mouseX = eventCasted.getX();
			mouseY = eventCasted.getY();
		}
		
		else {
			MouseEvent eventCasted = (MouseEvent) event;
			
			mouseX = eventCasted.getX();
			mouseY = eventCasted.getY();
		}
			
		//at this point the image should be less than or equal to the imagePool's width and height
		
		Double adjustedX = mouseX - this.getWidth()/2;
		Double adjustedY = mouseY - this.getHeight()/2;
		
		//set to 2 so that it keeps the look of having a border

		//if img is too far left or too far up
		//repositions so it is inside the box, accounts for borderBuffer
		if (adjustedX < borderBuffer) {
			adjustedX = borderBuffer;
		}
		if (adjustedY < borderBuffer) {
			adjustedY = borderBuffer;
		}
		
		//if img is too far right or too far down
		//repositions so it is inside the box, accounts for borderBuffer
		if (adjustedX + this.getWidth() > imagePool.getWidth() - borderBuffer) {	
			adjustedX -= (adjustedX + this.getWidth()) - (imagePool.getWidth() - borderBuffer);
		}
		if (adjustedY + this.getHeight() > imagePool.getHeight() - borderBuffer) {
			adjustedY -= (adjustedY + this.getHeight()) - (imagePool.getHeight() - borderBuffer);
		}
		
		this.setTranslateX(adjustedX);
		this.setTranslateY(adjustedY);
	}
	
	//makes it so image will resize as the imagePool resizes 
	public void bindProportionality() {
		Double ratioX = this.getWidth() / imagePool.getWidth();
		Double ratioY = this.getHeight() / imagePool.getHeight();
		
		this.maxWidthProperty().bind(imagePool.widthProperty().multiply(ratioX));
		this.minWidthProperty().bind(imagePool.widthProperty().multiply(ratioX));

		this.maxHeightProperty().bind(imagePool.heightProperty().multiply(ratioY));	
		this.minHeightProperty().bind(imagePool.heightProperty().multiply(ratioY));	
	}
	
	//for when ratios are already known, useful when reading from file
	public void bindProportionality(Double ratioX, Double ratioY) {
		this.maxWidthProperty().bind(imagePool.widthProperty().multiply(ratioX));
		this.minWidthProperty().bind(imagePool.widthProperty().multiply(ratioX));

		this.maxHeightProperty().bind(imagePool.heightProperty().multiply(ratioY));	
		this.minHeightProperty().bind(imagePool.heightProperty().multiply(ratioY));	
	}
	
	//makes it so image will keep relative position during resizing
	public void bindLayout() {
		Double ratioX = this.getTranslateX() / imagePool.getWidth();
		Double ratioY = this.getTranslateY() / imagePool.getHeight();
		
		this.translateXProperty().bind(imagePool.widthProperty().multiply(ratioX));
		this.translateYProperty().bind(imagePool.heightProperty().multiply(ratioY));
	}
	
	//for when ratios are already known, useful when reading from file
	public void bindLayout(Double ratioX, Double ratioY) {
		this.translateXProperty().bind(imagePool.widthProperty().multiply(ratioX));
		this.translateYProperty().bind(imagePool.heightProperty().multiply(ratioY));
	}
	
	//run for "activating" the fluid image wrapper, enables other functions to be run
	public void activate() {
		this.activated = true;
		this.setStyle("-fx-border-color: blue");
	}
	
	
	//a method for centering and shaping the images to size when
	//there are a limited number of images in the pool
	public void balanceImages() {
		if (imagePool.getChildren().size() == 1) {
			//if the height is longer than the width with respect to the golden ratio
			if (this.getHeight() / this.getWidth() >= goldenRatio) {
				//height must be matched to the imagePool
				this.maxHeightProperty().bind(imagePool.heightProperty());	
				this.minHeightProperty().bind(imagePool.heightProperty());	
				
				this.maxWidthProperty().bind(imagePool.heightProperty().multiply(this.getWidth() / this.getHeight()));	
				this.minWidthProperty().bind(imagePool.heightProperty().multiply(this.getWidth() / this.getHeight()));
				
				
				//there will be extra space at the sides
				this.translateYProperty().unbind();
				this.setTranslateY(borderBuffer/2);
				
				//sets the x to half the distance between width of image and width of the space
				this.translateXProperty().bind((imagePool.widthProperty().subtract(this.widthProperty()).divide(2)));
			}
			
			//if the height is less than the width with respect to the golden ratio
			else if (this.getHeight() / this.getWidth() <= goldenRatio) {
				//width must be matched to the imagePool
				this.maxWidthProperty().bind(imagePool.widthProperty());	
				this.minWidthProperty().bind(imagePool.widthProperty());	
				
				this.maxHeightProperty().bind(imagePool.heightProperty().multiply(this.getHeight() / this.getWidth()));	
				this.minHeightProperty().bind(imagePool.heightProperty().multiply(this.getHeight() / this.getWidth()));
				
				//there will be extra space at the sides
				this.translateXProperty().unbind();
				this.setTranslateX(borderBuffer/2);
				
				//sets the x to half the distance between width of image and width of the space
				this.translateYProperty().bind((imagePool.heightProperty().subtract(this.heightProperty()).divide(2)));
			}
			
			else {
				
			}
		}
		
		else if (imagePool.getChildren().size() == 2) {
			
			
			
			
		}
		
		else if (imagePool.getChildren().size() == 3) {
			
			
			
			
		}
	}
	
	
	
	EventHandler<MouseEvent> onClick = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			      FluidImageWrapper.this.onClick();
			   }});
	
	public void onClick() {
		this.activate();
	}
	
	
	
	EventHandler<MouseEvent> onMousePressed = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			   FluidImageWrapper.this.onMousePressed(event);
			   }});
	
	
	public void onMousePressed(MouseEvent event) {	
		System.out.println("bam");
		this.pressed = true;
	}
	
	
	
	
	EventHandler<MouseEvent> onMouseMoved = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			   FluidImageWrapper.this.onMouseMoved(event);
			   }});
	
	
	public void onMouseMoved(MouseEvent event) {	
		
		System.out.println((this.pressed == true));
		
		if ((this.activated == true) && (this.pressed == true)) {
			System.out.println("blah");
			this.setLocMiddleOfCursor(event);
		}
		
	}
	

	
	
	EventHandler<MouseEvent> onMouseReleased = (new EventHandler<MouseEvent>() { 
		   public void handle(MouseEvent event) { 
			   FluidImageWrapper.this.onMouseReleased(event);
			   }});
	
	
	public void onMouseReleased(MouseEvent event) {	
		System.out.println("boom");

		this.pressed = false;
	}

	public FluidImage getFluidImage() {
		return fluidImage;
	}
	
}