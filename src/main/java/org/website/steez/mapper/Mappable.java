package org.website.steez.mapper;


public interface Mappable<E, D> {

    D toDto(E entity);

    E toEntity(D dto);
}
