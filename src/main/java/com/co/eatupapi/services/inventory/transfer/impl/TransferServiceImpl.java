package com.co.eatupapi.services.inventory.transfer.impl;

import com.co.eatupapi.domain.inventory.product.Product;
import com.co.eatupapi.domain.inventory.transfer.Transfer;
import com.co.eatupapi.domain.inventory.transfer.TransferStatus;
import com.co.eatupapi.dto.inventory.transfer.TransferRequestDTO;
import com.co.eatupapi.dto.inventory.transfer.TransferResponseDTO;
import com.co.eatupapi.dto.inventory.transfer.TransferStatusUpdateDTO;
import com.co.eatupapi.repositories.inventory.location.LocationRepository;
import com.co.eatupapi.repositories.inventory.product.ProductRepository;
import com.co.eatupapi.repositories.inventory.transfer.TransferRepository;
import com.co.eatupapi.services.inventory.transfer.TransferService;
import com.co.eatupapi.utils.inventory.transfer.exceptions.TransferBusinessException;
import com.co.eatupapi.utils.inventory.transfer.exceptions.TransferNotFoundException;
import com.co.eatupapi.utils.inventory.transfer.exceptions.TransferValidationException;
import com.co.eatupapi.utils.inventory.transfer.mapper.TransferMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceImpl implements TransferService {

    private static final String ORIGIN_ROLE = "origen";
    private static final String DESTINATION_ROLE = "destino";

    private record TransferProducts(Product originProduct, Product destinationProduct) {
    }

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    public TransferServiceImpl(TransferRepository transferRepository,
                               TransferMapper transferMapper,
                               ProductRepository productRepository,
                               LocationRepository locationRepository) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public TransferResponseDTO create(TransferRequestDTO request) {
        TransferProducts products = validateAndLoadRequest(request);
        Transfer transfer = transferMapper.toDomain(request);
        transfer.setEstado(TransferStatus.EN_PROCESO);
        transfer.setStock(products.originProduct().getStock());
        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    @Override
    @Transactional
    public TransferResponseDTO updateStatus(Long id, TransferStatusUpdateDTO statusUpdate) {
        validateId(id);
        if (statusUpdate == null || statusUpdate.estado() == null) {
            throw new TransferValidationException("El estado es obligatorio");
        }

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new TransferNotFoundException("Transferencia no encontrada con id: " + id));

        validateStatusTransition(transfer.getEstado(), statusUpdate.estado());
        if (statusUpdate.estado() == TransferStatus.COMPLETADO) {
            applyInventoryMovement(transfer);
        }
        transfer.setEstado(statusUpdate.estado());
        return transferMapper.toResponse(transferRepository.save(transfer));
    }

    @Override
    public TransferResponseDTO findById(Long id) {
        validateId(id);
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new TransferNotFoundException("Transferencia no encontrada con id: " + id));
        return transferMapper.toResponse(transfer);
    }

    @Override
    public List<TransferResponseDTO> findAll() {
        return transferRepository.findAll()
                .stream()
                .map(transferMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransferResponseDTO> findAllInTransit() {
        return transferRepository.findByEstado(TransferStatus.EN_PROCESO)
                .stream()
                .map(transferMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransferResponseDTO> findAllCompleted() {
        return transferRepository.findByEstado(TransferStatus.COMPLETADO)
                .stream()
                .map(transferMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransferResponseDTO> findAllCancelled() {
        return transferRepository.findByEstado(TransferStatus.CANCELADO)
                .stream()
                .map(transferMapper::toResponse)
                .toList();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new TransferValidationException("El id de la transferencia es obligatorio y debe ser mayor a cero");
        }
    }

    private void validateRequest(TransferRequestDTO request) {
        if (request == null) {
            throw new TransferValidationException("La solicitud no puede estar vacía");
        }
        validateLocations(request);
        validateCoreFields(request);
        validateDates(request);
        validateQuantity(request);
    }

    private TransferProducts validateAndLoadRequest(TransferRequestDTO request) {
        validateRequest(request);
        validateLocationExistsAndActive(request.sedeOrigen(), ORIGIN_ROLE);
        validateLocationExistsAndActive(request.sedeDestino(), DESTINATION_ROLE);

        Product originProduct = findProductByNameAndLocation(request.producto(), request.sedeOrigen(), ORIGIN_ROLE);
        Product destinationProduct = findProductByNameAndLocation(request.producto(), request.sedeDestino(), DESTINATION_ROLE);
        validateStockAndQuantity(originProduct.getStock(), request.cantidad());
        return new TransferProducts(originProduct, destinationProduct);
    }

    private void validateLocations(TransferRequestDTO request) {
        if (request.sedeOrigen() == null || request.sedeOrigen().isBlank()) {
            throw new TransferValidationException("La sede de origen es obligatoria y debe ser válida");
        }
        if (request.sedeDestino() == null || request.sedeDestino().isBlank()) {
            throw new TransferValidationException("La sede de destino es obligatoria y debe ser válida");
        }
        if (request.sedeOrigen().trim().equals(request.sedeDestino().trim())) {
            throw new TransferBusinessException("La sede de origen no puede ser igual a la sede de destino");
        }
    }

    private void validateCoreFields(TransferRequestDTO request) {
        if (request.fechaEnvio() == null) {
            throw new TransferValidationException("La fecha de transferencia es obligatoria");
        }
        if (request.fechaLlegada() == null) {
            throw new TransferValidationException("La fecha de llegada es obligatoria");
        }
        if (request.responsable() == null || request.responsable().isBlank()) {
            throw new TransferValidationException("El responsable es obligatorio");
        }
        if (request.producto() == null || request.producto().isBlank()) {
            throw new TransferValidationException("El producto es obligatorio y debe ser válido");
        }
    }

    private void validateDates(TransferRequestDTO request) {
        if (request.fechaEnvio().toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new TransferValidationException("La fecha de envío no puede ser anterior a la fecha actual");
        }
        if (request.fechaLlegada().toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new TransferValidationException("La fecha de llegada no puede ser anterior a la fecha actual");
        }
        if (request.fechaLlegada().isBefore(request.fechaEnvio())) {
            throw new TransferValidationException("La fecha de llegada no puede ser anterior a la fecha de envío");
        }
    }

    private void validateQuantity(TransferRequestDTO request) {
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new TransferValidationException("La cantidad debe ser mayor a cero");
        }
    }

    private void validateStockAndQuantity(BigDecimal stock, Integer cantidad) {
        if (stock == null || stock.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransferBusinessException("El producto tiene un stock inválido para realizar la transferencia");
        }
        if (BigDecimal.valueOf(cantidad.longValue()).compareTo(stock) > 0) {
            throw new TransferBusinessException("La cantidad a transferir no puede superar el stock disponible");
        }
    }

    private void applyInventoryMovement(Transfer transfer) {
        TransferProducts products = loadProductsFromTransfer(transfer);
        BigDecimal quantity = BigDecimal.valueOf(transfer.getCantidad().longValue());

        validateStockAndQuantity(products.originProduct().getStock(), transfer.getCantidad());

        products.originProduct().setStock(products.originProduct().getStock().subtract(quantity));
        products.destinationProduct().setStock(products.destinationProduct().getStock().add(quantity));

        productRepository.save(products.originProduct());
        productRepository.save(products.destinationProduct());
        transfer.setStock(products.originProduct().getStock());
    }

    private TransferProducts loadProductsFromTransfer(Transfer transfer) {
        Product originProduct = findProductByNameAndLocation(transfer.getProducto(), transfer.getSedeOrigen(), ORIGIN_ROLE);
        Product destinationProduct = findProductByNameAndLocation(transfer.getProducto(), transfer.getSedeDestino(), DESTINATION_ROLE);
        return new TransferProducts(originProduct, destinationProduct);
    }

    private Product findProductByNameAndLocation(String productName, String locationId, String role) {
        UUID locationUuid;
        try {
            locationUuid = UUID.fromString(locationId);
        } catch (IllegalArgumentException ex) {
            throw new TransferValidationException("La sede de " + role + " debe ser un UUID válido");
        }

        return productRepository.findAll().stream()
                .filter(product -> locationUuid.equals(product.getLocationId()))
                .filter(product -> product.getName() != null)
                .filter(product -> product.getName().trim().equalsIgnoreCase(productName.trim()))
                .findFirst()
                .orElseThrow(() -> new TransferNotFoundException(
                        "No existe el producto '" + productName + "' en la sede de " + role
                ));
    }

    private void validateLocationExistsAndActive(String locationId, String role) {
        UUID parsedLocationId;
        try {
            parsedLocationId = UUID.fromString(locationId);
        } catch (IllegalArgumentException ex) {
            throw new TransferValidationException("La sede de " + role + " debe ser un UUID válido");
        }

        locationRepository.findById(parsedLocationId)
                .filter(com.co.eatupapi.repositories.inventory.location.LocationEntity::isActive)
                .orElseThrow(() -> new TransferNotFoundException("La sede de " + role + " no existe o está inactiva"));
    }

    private void validateStatusTransition(TransferStatus currentStatus, TransferStatus nextStatus) {
        if (currentStatus == nextStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {
            case EN_PROCESO -> nextStatus == TransferStatus.COMPLETADO || nextStatus == TransferStatus.CANCELADO;
            case COMPLETADO, CANCELADO -> false;
        };

        if (!validTransition) {
            throw new TransferBusinessException(
                    "No se permite cambiar el estado de " + currentStatus + " a " + nextStatus
            );
        }
    }
}
