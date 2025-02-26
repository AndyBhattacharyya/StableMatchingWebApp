import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


//preamble for processing POST requests w/ multipart-form
@MultipartConfig(fileSizeThreshold = 1024*1024, maxFileSize = 1024*1024*5, maxRequestSize = 1024*1024*5*5)
public class FileUpload extends HttpServlet {

    private int fileUploads = 0;
    private File UPLOAD_DIRECTORY;

   public void init(ServletConfig config) throws ServletException {
       super.init(config);
       UPLOAD_DIRECTORY = new File(getServletContext().getRealPath("") + "/" + "uploads");
       UPLOAD_DIRECTORY.mkdir();
   }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /*
        //find formal way to initialize the upload directory
        if(fileUploads==0){
            UPLOAD_DIRECTORY = new File(getServletContext().getRealPath("") + "/" + "uploads");
            UPLOAD_DIRECTORY.mkdir();
        }
        */

        //How to exclude this from the request
        for(Part part : req.getParts()) {
            //look into ways to better handle the possible null pointer exception
            if(part.getContentType()!= null && part.getContentType().equals("image/png")){
                //begin processing
                try(
                        InputStream img_data = part.getInputStream();
                        FileOutputStream fos = new FileOutputStream(new File(UPLOAD_DIRECTORY, "image" + ++fileUploads + ".png"));
                        ){
                    while(img_data.available() > 0){
                        fos.write(img_data.read());
                        fos.flush();
                    }
                } catch(IOException e){System.out.println(e);}
            }
        }
    }



}
