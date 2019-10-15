/*
 *
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.github.meifans.apidoc.maven.plugin.spring.raw;

import com.github.meifans.apidoc.maven.plugin.spring.raw.model.Pet;
import com.github.meifans.apidoc.maven.plugin.spring.raw.model.Responses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.NotFoundException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ResponseHeader;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(value = "/api/pet", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
@Api(value = "/pet", description = "Operations about pets")
public class PetDemoController {

    @RequestMapping(value = "/{petId}", method = GET)
    @ApiOperation(
            value = "Find pet by ID", notes = "Returns a pet when ID < 10. ID > 10 or non-integers will simulate API " +
            "error conditions",
            response = Pet.class,
            responseHeaders = {
                    @ResponseHeader(name = "header4", response = String.class),
                    @ResponseHeader(name = "header3", response = String.class)
            },
            authorizations = {
                    @Authorization(value = "api_key"),
                    @Authorization(value = "petstore_auth", scopes = {
                            @AuthorizationScope(scope = "write:pets", description = ""),
                            @AuthorizationScope(scope = "read:pets", description = "")
                    })})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied", responseHeaders = {
                    @ResponseHeader(name = "header2", response = String.class),
                    @ResponseHeader(name = "header1", response = String.class)
            }),
            @ApiResponse(code = 404, message = "Pet not found")}
    )
    public ResponseEntity<Pet> getPetById(
            @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
            @PathVariable("petId") String petId)
            throws NotFoundException {
        Pet pet = new Pet();
        if (null != pet) {
            return Responses.ok(pet);
        } else {
            throw new NotFoundException("Pet not found");
        }
    }

    @RequestMapping(method = POST)
    @ApiOperation(value = "Add a new pet to the store",
            authorizations = @Authorization(value = "petstore_auth", scopes = {
                    @AuthorizationScope(scope = "write:pets", description = ""),
                    @AuthorizationScope(scope = "read:pets", description = "")
            }))
    @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
    public ResponseEntity<String> addPet(
            @ApiParam(value = "Pet object that needs to be added to the store", required = true) @RequestBody Pet pet) {
        return Responses.ok("SUCCESS");
    }

    @RequestMapping(method = PUT)
    @ApiOperation(value = "Update an existing pet",
            authorizations = @Authorization(value = "petstore_auth", scopes = {
                    @AuthorizationScope(scope = "write:pets", description = ""),
                    @AuthorizationScope(scope = "read:pets", description = "")
            }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Pet not found"),
            @ApiResponse(code = 405, message = "Validation exception")})
    public ResponseEntity<String> updatePet(
            @ApiParam(value = "Pet object that needs to be added to the store", required = true) @RequestBody Pet pet) {
        return Responses.ok("SUCCESS");
    }

    @RequestMapping(value = "/findByStatus", method = GET)
    @ApiOperation(
            value = "Finds Pets by status",
            notes = "Multiple status values can be provided with comma-separated strings",
            response = Pet.class,
            responseContainer = "List",
            authorizations = @Authorization(value = "petstore_auth", scopes = {
                    @AuthorizationScope(scope = "write:pets", description = ""),
                    @AuthorizationScope(scope = "read:pets", description = "")
            }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid status value")})
    public ResponseEntity<List<Pet>> findPetsByStatus(
            @ApiParam(value = "Status values that need to be considered for filter",
                    required = true,
                    defaultValue = "available",
                    allowableValues = "available,pending,sold",
                    allowMultiple = true)
            @RequestParam("status") String status) {
        return Responses.ok(Collections.emptyList());
    }

    @RequestMapping(value = "/findByTags", method = GET)
    @ApiOperation(
            value = "Finds Pets by tags",
            notes = "Multiple tags can be provided with comma-separated strings. Use tag1, tag2, tag3 for testing.",
            response = Pet.class,
            responseContainer = "List",
            authorizations = @Authorization(value = "petstore_auth", scopes = {
                    @AuthorizationScope(scope = "write:pets", description = ""),
                    @AuthorizationScope(scope = "read:pets", description = "")
            }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid tag value")})
    @Deprecated
    /** TODO: This renders the parameter as
     "name": "tags",
     "in": "query",
     "description": "Tags to filter by",
     "required": false,
     "type": "array",
     "items": {"type": "string"},
     "collectionFormat": "multi" */
    public ResponseEntity<List<Pet>> findPetsByTags(
            @ApiParam(
                    value = "Tags to filter by",
                    required = true,
                    allowMultiple = true)
            @RequestParam("tags") String tags) {
        return Responses.ok(newArrayList());
    }

    @RequestMapping(value = "/findPetsHidden", method = GET)
    @ApiOperation(
            value = "Finds Pets (hidden)",
            notes = "Hidden method",
            response = Pet.class,
            responseContainer = "List",
            hidden = true,
            authorizations = @Authorization(value = "petstore_auth", scopes = {
                    @AuthorizationScope(scope = "write:pets", description = ""),
                    @AuthorizationScope(scope = "read:pets", description = "")
            }))
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid tag value")})
    public ResponseEntity<List<Pet>> findPetsHidden(
            @ApiParam(
                    value = "Tags to filter by",
                    required = true,
                    allowMultiple = true)
            @RequestParam("tags") String tags) {
        return Responses.ok(newArrayList());
    }

}
