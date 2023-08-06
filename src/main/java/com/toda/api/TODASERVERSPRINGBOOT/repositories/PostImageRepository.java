package com.toda.api.TODASERVERSPRINGBOOT.repositories;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,Long> {
}
