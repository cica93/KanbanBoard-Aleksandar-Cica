package com.example.Kanban.Board.utilities;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericConverter<M, D> {
    public abstract D convertModelToDTOModel(M model);
    public abstract M convertDTOModelToModel(D dtoModel);

    public List<D> convertListOfModelsToDTOModel(Collection<M> models) {
        return models == null ? null
                : models.stream().filter(x -> x != null).map(m -> convertModelToDTOModel(m)).filter(e -> e != null)
                        .collect(Collectors.toList());
    }

    public List<M> convertListOfDTOModelsTOModel(Collection<D> dtoModels) {
        return dtoModels == null ? null
                : dtoModels.stream().filter(x -> x != null).map(d -> convertDTOModelToModel(d)).filter(e -> e != null)
                        .collect(Collectors.toList());
    }

}
