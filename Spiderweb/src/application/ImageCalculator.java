package application;

import java.lang.reflect.Array;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ImageCalculator {
	
	//When an image is added to the image pool...
	
	//first its height and width will be calculated relative to the current size of the pane
	
	//if the width or height is bigger than the width or height, then it is resized so that the entire image fits into the pool
	
	//when the window is resized, the proportions of the image pool will stay the same. Also, the images will stay the same size
	//in proportion to the window, so they have to be resized whenever the window is resized 

	public double[] getRelativeMaxSize(Image img, AnchorPane anchorOfImageView) {
				
		double anchorWidth = anchorOfImageView.getWidth();
		double anchorHeight = anchorOfImageView.getHeight();
		
		System.out.println(anchorWidth);
				
		double imageWidth = img.getWidth();
		double imageHeight = img.getHeight();
		
		System.out.println(imageWidth);

		
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
		
	    return toReturn;
		
		
	}
}
