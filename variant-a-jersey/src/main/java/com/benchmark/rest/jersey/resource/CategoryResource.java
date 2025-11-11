package com.benchmark.rest.jersey.resource;

import com.benchmark.rest.jersey.dto.ItemDTO;
import com.benchmark.rest.jersey.model.Category;
import com.benchmark.rest.jersey.model.Item;
import com.benchmark.rest.jersey.repository.CategoryRepository;
import com.benchmark.rest.jersey.repository.ItemRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final CategoryRepository repository;
    private final ItemRepository itemRepository;

    public CategoryResource(CategoryRepository repository, ItemRepository itemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
    }

    @GET
    public Response getAllCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        Page<Category> pageResult = repository.findAll(PageRequest.of(page, size));
        return Response.ok(pageResult).build();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        return repository.findById(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response createCategory(Category category, @Context UriInfo uriInfo) {
        Category saved = repository.save(category);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(saved.getId()))
                .build();
        return Response.created(location).entity(saved).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, Category category) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setCode(category.getCode());
                    Category updated = repository.save(existing);
                    return Response.ok(updated).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        if (!repository.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.deleteById(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/items")
    public Response getCategoryItems(
            @PathParam("id") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        if (!repository.existsById(categoryId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        PageRequest pageable = PageRequest.of(page, size);
        List<Item> items = itemRepository.findByCategoryIdJoinFetch(categoryId);
        
        List<ItemDTO> dtos = items.stream()
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getSku(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getCategory().getCode()
                ))
                .collect(Collectors.toList());

        Page<ItemDTO> responsePage = new PageImpl<>(dtos, pageable, items.size());
        return Response.ok(responsePage).build();
    }
}

