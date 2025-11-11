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
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemResource(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    @GET
    public Response getItems(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        PageRequest pageable = PageRequest.of(page, size);
        List<Item> items;
        long total;

        if (categoryId != null) {
            items = itemRepository.findByCategoryIdJoinFetch(categoryId);
            total = items.size();
        } else {
            Page<Item> pageResult = itemRepository.findAllWithCategory(pageable);
            items = pageResult.getContent();
            total = pageResult.getTotalElements();
        }

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

        Page<ItemDTO> responsePage = new PageImpl<>(dtos, pageable, total);
        return Response.ok(responsePage).build();
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") Long id) {
        return itemRepository.findById(id)
                .map(item -> new ItemDTO(
                        item.getId(),
                        item.getSku(),
                        item.getName(),
                        item.getPrice(),
                        item.getStock(),
                        item.getCategory().getCode()
                ))
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    public Response createItem(Item item, @Context UriInfo uriInfo) {
        if (item.getCategory() == null || item.getCategory().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Category category = categoryRepository.findById(item.getCategory().getId())
                .orElse(null);
        if (category == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        item.setCategory(category);
        Item saved = itemRepository.save(item);
        ItemDTO response = new ItemDTO(
                saved.getId(),
                saved.getSku(),
                saved.getName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getCategory().getCode()
        );

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(saved.getId()))
                .build();
        return Response.created(location).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") Long id, Item item) {
        return itemRepository.findById(id)
                .map(existing -> {
                    existing.setName(item.getName());
                    existing.setPrice(item.getPrice());
                    existing.setStock(item.getStock());
                    existing.setSku(item.getSku());
                    
                    if (item.getCategory() != null && item.getCategory().getId() != null) {
                        categoryRepository.findById(item.getCategory().getId())
                                .ifPresent(existing::setCategory);
                    }
                    
                    Item updated = itemRepository.save(existing);
                    ItemDTO response = new ItemDTO(
                            updated.getId(),
                            updated.getSku(),
                            updated.getName(),
                            updated.getPrice(),
                            updated.getStock(),
                            updated.getCategory().getCode()
                    );
                    return Response.ok(response).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") Long id) {
        if (!itemRepository.existsById(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        itemRepository.deleteById(id);
        return Response.noContent().build();
    }
}

