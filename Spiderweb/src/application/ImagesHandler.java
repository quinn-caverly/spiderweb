package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public class ImagesHandler {
    
    //this list will be universal even after nature of the image panel changes
    private ArrayList<ImageView> image_list = new ArrayList<ImageView>();
    
    //how the images are labeled in the file
    private String img_label = "image";
    
    //where the images labels start counting from
    private Integer img_labels_start = 0;
	
    //must take the images as constructors because this is not an FXML controller class
    
	
	public void saveImagesToFile(String path) throws IOException {
				
		Iterator<ImageView> iter = image_list.iterator();
		
		
		Integer img_num = img_labels_start;
		while (iter.hasNext()) {
			ImageView current_img = iter.next();
			
			File file = new File(path + img_label + img_num.toString() + ".png");
			ImageIO.write(SwingFXUtils.fromFXImage(current_img.getImage(), null), "png", file);
			
			img_num += 1;
		}
		
	}
		
	public void loadImagesFromFile(String path) throws IOException {
		
		Integer img_num = img_labels_start;
		
		Integer length = new File(path).list().length;
		System.out.println(length);
				
		
		File file = new File(path + img_label + img_num.toString() + ".png");
		
		
		
	}

}
