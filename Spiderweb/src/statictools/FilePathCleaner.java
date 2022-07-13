package statictools;

public class FilePathCleaner {
	
	public static String returnCleanedFilePath(String filePath) {
		
		filePath = filePath.replace("\\","/");
		
		return filePath;
	}
}
