package com.irem.demo.service;

import com.irem.demo.dto.RegionResponse;
import com.irem.demo.mapper.RegionMapper;
import com.irem.demo.model.Region; // Model sınıfını import ediyoruz
import com.irem.demo.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {
    @Mock
    private RegionRepository RegionRepository;

    @Mock
    private RegionMapper RegionMapper;

    @InjectMocks
    private RegionService RegionService;

    @Test
    void testGetAllRegions(){

        Region region1= new Region();
        region1.setId(1L);
        region1.setCountryCode("TR");
        region1.setCountryName("Türkiye");

        List<Region> mockRegionList= Arrays.asList(region1);

        when(RegionRepository.findAll()).thenReturn(mockRegionList); //findall returns all instances of the type
//map tipinde list
         List<RegionResponse> mockResponseList = Arrays.asList(
                new RegionResponse(1L,"Türkiye","TR")           
        );


        when(RegionMapper.toRegionResponseList(mockRegionList)).thenReturn(mockResponseList);

        List<RegionResponse> result = RegionService.getAllRegions(); //burda methodu kullanarak result list e koydum

        assertEquals(1L,result.get(0).getId()); //list'teki 0. index in id'si gerçekten Long 1 mi
        assertEquals("TR", result.get(0).getCountryCode());//countrycode gerçekten tr mi
        assertEquals("Türkiye", result.get(0).getCountryName()); //country name gerçekten türkiye mi
    }
}
