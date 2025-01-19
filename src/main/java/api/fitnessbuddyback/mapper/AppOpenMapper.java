package api.fitnessbuddyback.mapper;

import api.fitnessbuddyback.dto.AppOpenDTO;
import api.fitnessbuddyback.entity.AppOpen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppOpenMapper {

    @Mapping(source = "user.id", target = "userId")
    AppOpenDTO toDTO(AppOpen appOpen);

    List<AppOpenDTO> toDTO(List<AppOpen> appOpens);
}
