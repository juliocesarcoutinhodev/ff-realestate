package br.com.fabriciofaceroli.property.adapter.in.web.mapper;

import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertyDetailResponse;
import br.com.fabriciofaceroli.property.adapter.in.web.dto.PropertySummaryResponse;
import br.com.fabriciofaceroli.property.domain.model.CategoryInfo;
import br.com.fabriciofaceroli.property.domain.model.PhotoSummary;
import br.com.fabriciofaceroli.property.domain.model.Property;
import br.com.fabriciofaceroli.property.domain.model.PropertyDetail;
import br.com.fabriciofaceroli.shared.response.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PropertyWebMapper {

    @Mapping(target = "coverPhoto", expression = "java(null)")
    PropertySummaryResponse toSummaryResponse(Property property);

    PropertyDetailResponse toDetailResponse(PropertyDetail propertyDetail);

    PropertyDetailResponse.CategorySummary toCategorySummary(CategoryInfo categoryInfo);

    PropertyDetailResponse.PhotoResponse toPhotoResponse(PhotoSummary photoSummary);

    default PageResponse<PropertySummaryResponse> toPageResponse(Page<Property> page) {
        List<PropertySummaryResponse> content = page.getContent()
                .stream()
                .map(this::toSummaryResponse)
                .toList();
        return new PageResponse<>(content, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }
}
