package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.runner.RunWith;
import org.opensrp.repository.ManifestRepository;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.domain.Manifest;


@RunWith(PowerMockRunner.class)
public class ManifestServiceTest {

    private ManifestService manifestService;

    private ManifestRepository manifestRepository;

    @Before
    public void setUp() {
        manifestRepository = mock(ManifestRepository.class);
        manifestService = new ManifestService();
        manifestService.setManifestRepository(manifestRepository);
    }
    

    private static Manifest initTestManifest(){
        Manifest manifest = new Manifest();
        String identifier = "mani1234";
        String appVersion = "1234234";
        String json = "{}";
        String appId = "1234567op";

        manifest.setAppId(appId);
        manifest.setAppVersion(appVersion);
        manifest.setIdentifier(identifier);
        manifest.setJson(json);
        return manifest;
    }

 @Test
    public void testGetManifest() {
        List<Manifest> expectedManifest = new ArrayList<>();
        expectedManifest.add(initTestManifest());
        when(manifestRepository.getAll()).thenReturn(expectedManifest);

        List<Manifest> actutalmanifest = manifestService.getAllManifest();
        verify(manifestRepository).getAll();
        assertEquals(1, actutalmanifest.size());
        assertEquals("mani1234", actutalmanifest.get(0).getIdentifier());
    }

    @Test
    public void testGetManifestByIdentifier() {
        Manifest expectedManifest = initTestManifest();
        when(manifestRepository.get(anyString())).thenReturn(expectedManifest);
        Manifest actutalManifest = manifestService.getManifest(expectedManifest.getIdentifier());

        verify(manifestRepository).get(anyString());
        assertNotNull(actutalManifest);
        assertEquals("mani1234", actutalManifest.getIdentifier());
    }

    @Test
    public void testAddOrUpdateManifest() {
        when(manifestRepository.get(anyString())).thenReturn(null);
        Manifest manifest = initTestManifest();
   	    assertNotNull(manifest);
        manifestService.addOrUpdateManifest(manifest);
   	    verify(manifestRepository).add(eq(manifest));
    }
    
    @Test
    public void testAddManifest() {
    	 when(manifestRepository.get(anyString())).thenReturn(null);
    	 Manifest manifest = initTestManifest();
    	 assertNotNull(manifest);
         manifestService.addManifest(manifest);
         verify(manifestRepository).add(eq(manifest));
    }
    
    @Test
    public void testUpdateManifest() {
    	 when(manifestRepository.get(anyString())).thenReturn(null);
    	 Manifest manifest = initTestManifest();
    	 assertNotNull(manifest);
         manifestService.updateManifest(manifest);
         verify(manifestRepository).update(eq(manifest));
    }
       
    @Test
    public void testSaveManifest() {
    	 when(manifestRepository.get(anyString())).thenReturn(null); 
    	 List<Manifest> manifestList = new ArrayList<>();
    	 Manifest manifest = initTestManifest();
    	 assertNotNull(manifest);
    	 manifestList.add(manifest);
         manifestService.saveManifests(manifestList);
         verify(manifestRepository).add(eq(manifest));  
         }
    
    @Test
    public void testGetManifestByAppId() {
        Manifest expectedManifest = initTestManifest();
        when(manifestRepository.getManifestByAppId(anyString())).thenReturn(expectedManifest);

        Manifest actutalManifest = manifestService.getManifestByAppId(expectedManifest.getAppId());
        verify(manifestRepository).getManifestByAppId(anyString());
        assertNotNull(actutalManifest);
        assertEquals("1234567op", actutalManifest.getAppId());
   
    }   
    
    @Test
    public void testDeleteShouldCallRepostorySafeRemoveMethod() {
        when(manifestRepository.get(anyString())).thenReturn(initTestManifest());
        Manifest manifest = initTestManifest();
        manifestService.deleteManifest(manifest);
        verify(manifestRepository).safeRemove(eq(manifest));
    }


}
