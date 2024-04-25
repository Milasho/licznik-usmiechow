package smilecounter.web.endpoints.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smilecounter.core.affective.enums.AffectiveServices;
import smilecounter.core.affective.model.Face;
import smilecounter.core.data.model.LocalisationData;
import smilecounter.core.data.model.Snapshot;
import smilecounter.core.utils.ImagesHelper;
import smilecounter.web.model.ChartsData;
import smilecounter.web.model.SimpleSnapshot;
import smilecounter.web.services.SmileCounterService;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/smiles")
@Api(value = "/smiles")
public class SmilesRestEndpoint {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject private SmileCounterService service;
    @Context private UriInfo uriInfo;

    @GET
    @Path("/counter")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns global amount of smiles detected by application")
    public Long getSmilesCount(@QueryParam("dateFrom") Long dateFrom, @QueryParam("dateTo") Long dateTo){
        if(dateFrom != null && dateTo != null){
            return service.getGlobalSmilesCounter(dateFrom, dateTo);
        }
        else{
            return service.getGlobalSmilesCounter();
        }
    }

    @GET
    @Path("/photosCounter")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns global amount of smiles with photo")
    public int getSmilesWithPhotoCount(){
       return service.getSmilesWithPhoto().size();
    }

    @GET
    @Path("/chartsData/{dateFrom}/{dateTo}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns list of smiles per day between two dates")
    public ChartsData getChartsData (@PathParam("dateFrom") Long dateFrom, @PathParam("dateTo") Long dateTo){
        return service.getChartsData(dateFrom, dateTo);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns list of detected smiles")
    public List<SimpleSnapshot> getSmiles () {
        List<Snapshot> smilesWithPhoto = service.getSmilesWithPhoto();
        List<SimpleSnapshot> photos = new ArrayList<>();

        for(Snapshot snapshot : smilesWithPhoto){
            SimpleSnapshot simple = new SimpleSnapshot();
            simple.setLink(uriInfo.getBaseUri().toString() + "smiles/smile/" + snapshot.getId());
            photos.add(simple);
        }

        return photos;
    }

    @GET
    @Path("/smile/{smileId}/")
    @Produces("image/png")
    @ApiOperation(value="Returns list of detected smiles")
    public Response getSmileWithId (@PathParam("smileId") String smileId){
        Snapshot snapshot = service.getSmile(smileId);
        return buildImageResponse(snapshot.getContent());
    }

    @POST
    @Path("/detect")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns list of detected smiles on sent snapshot")
    public Snapshot detectedSmiles(Snapshot snapshot, @QueryParam("service") AffectiveServices affectiveService){
        if(affectiveService == null){
            affectiveService = AffectiveServices.LUXAND;
        }
        snapshot.setDetectedSmiles(service.detectSmiles(snapshot, affectiveService));
        return snapshot;
    }

    @POST
    @Path("/detectAndSave")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns list of detected smiles on sent snapshot")
    public void detectedSmilesAndSave(List<Snapshot> snapshots){
        if(snapshots != null && snapshots.size() > 0){
            List<Snapshot> snapshotsToSave = new ArrayList<>();
            for(Snapshot snapshot : snapshots){
                List<Face> faces = service.detectSmiles(snapshot, AffectiveServices.LUXAND);
                if(faces.size() > 0){
                    snapshot.setDetectedSmiles(faces);
                    if(!Boolean.TRUE.equals(snapshot.isPermissionToSave())){
                        snapshot.setContent(null);
                    }
                    snapshot.setDate(new Date());
                    snapshot.setLocalisation("Smilecounter Web Application");
                    snapshotsToSave.add(snapshot);
                }
            }

            LOGGER.info("Recieved {} smiles to save, saving {}...", snapshots.size(), snapshotsToSave.size());
            service.saveSnapshots(snapshotsToSave);
        }
    }

    @GET
    @Path("/localisations/{limit}/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Returns list of best localisations limited by parameter")
    public List<LocalisationData> getBestLocalisations (@PathParam("limit") Integer limit){
        return service.getBestLocalisations(limit);
    }

    private Response buildImageResponse(String base64){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            String b = base64.split(",")[1];
            ImageIO.write(ImagesHelper.convertBase64ToBufferedImage(b), "png", baos);
        }
        catch (IOException e) {
            return Response.noContent().build();
        }
        byte[] imageData = baos.toByteArray();
        return Response.ok(imageData).build();
    }
}
