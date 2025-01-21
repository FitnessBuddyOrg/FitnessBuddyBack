package api.fitnessbuddyback.mapper;

import api.fitnessbuddyback.dto.RoutineDTO;
import api.fitnessbuddyback.entity.Routine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoutineMapper {

    @Mapping(source = "user.id", target = "userId")
    RoutineDTO toDTO(Routine routine);

    List<RoutineDTO> toDTO(List<Routine> routines);
}
