package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import nz.ac.auckland.concert.common.message.Messages;


/**
 * Class for downloading images from aws
 *
 */
public class ImagesDownLoader {

	private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
	private static final String AWS_SECRET_ACCESS_KEY = "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";

	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

	// Download directory - a directory named "PerformerImage" in the user's home directory.
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String USER_DIRECTORY = System.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY + FILE_SEPARATOR + "PerformerImage";

	private static Logger _logger = LoggerFactory.getLogger(ImagesDownLoader.class);
		
	public static Image downLoadImage(String imageName) throws ServiceException {
		
		// Create download directory if it doesn't already exist.
		File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
		downloadDirectory.mkdir();
		
		try {
			//Create an AmazonS3 object that represents a connection with the remote S3 service.
			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
							AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
			AmazonS3 s3 = AmazonS3ClientBuilder
							.standard()
							.withRegion(Regions.AP_SOUTHEAST_2)
							.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
							.build();
			
			//CHeck if the bucket has the image				
			List<String> nameList = getImageNames(s3);
			if (!nameList.contains(imageName)){
				throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
			}
			
			//Request file
		    S3Object s3image = s3.getObject(AWS_BUCKET, imageName);
		    S3ObjectInputStream s3input = s3image.getObjectContent();
			
		

		    //Get image
		    FileOutputStream fileReceived = new FileOutputStream(new File(DOWNLOAD_DIRECTORY + "/images" +imageName));
			    
		    //Build the image in local directory
		    byte[] read_buffer = new byte[1024];
		    int read_len = 0;
		    while ((read_len = s3input.read(read_buffer)) > 0) {
		    	fileReceived.write(read_buffer, 0, read_len);
		    }
		    
		    //Close all connection
		    s3input.close();
		    fileReceived.close();
		    
		    //Convert to a java.awt.Image
		    return convertFileToImage(DOWNLOAD_DIRECTORY + "/" + imageName);
		    
		//Errors handling
		} catch (AmazonS3Exception e){
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} catch (AmazonServiceException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} catch (AmazonClientException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} catch (FileNotFoundException e) {
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		} catch (IOException e){
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);	
		}
	}
	
	
	/**
	 * Get the file into a java.awt.Image object
	 */
	private static Image convertFileToImage(String directory){
		
		Image image = null;
		
		try {
			File file = new File(directory);
			image = ImageIO.read(file);
		} catch (IOException e) {
		}
		return image;
	}
	
	/**
	 * Finds all image stored in a bucket named AWS_BUCKET.
	 * 
	 * @param s3 the AmazonS3 connection.
	 * 
	 * @return a List of images names.
	 * 
	 */
	private static List<String> getImageNames(AmazonS3 s3) {
		
		//A list to return
		List<String> nameList = new ArrayList();
		
		try{
			//Get all objects' summary
			ObjectListing allObjects = s3.listObjects(AWS_BUCKET);
			List<S3ObjectSummary> objectsList = allObjects.getObjectSummaries();
			
			//Get the objects' keys
			for (S3ObjectSummary image: objectsList) {
				nameList.add(image.getKey());
			}
			return nameList;
		
		//Throw the exception as it is when error occurs
		} catch (AmazonS3Exception e){
			throw e;
		} catch (AmazonServiceException e) {
			throw e;
		}
	}
}

