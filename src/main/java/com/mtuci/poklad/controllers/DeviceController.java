package com.mtuci.poklad.controllers;

import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.requests.DataDeviceRequest;
import com.mtuci.poklad.service.impl.DeviceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/settings/device")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DeviceController {

    private final DeviceServiceImpl deviceService;

    /**
     * Сохранение нового устройства.
     *
     * @param userId     ID пользователя
     * @param name       Название устройства
     * @param macAddress MAC-адрес устройства
     * @return ответ с данными устройства и статусом
     */

    @PostMapping
    public ResponseEntity<?> save(@RequestParam("user_id") Long userId,
                                  @RequestParam("name") String name,
                                  @RequestParam("mac_address") String macAddress) {
        try {
            // Создаем DataDeviceRequest
            DataDeviceRequest deviceRequest = new DataDeviceRequest(null, userId, name, macAddress);

            // Вызываем метод save
            Device device = deviceService.save(deviceRequest);

            return ResponseEntity.ok(deviceRequest);
        } catch (Exception e) {
            return handleError(e);
        }
    }



    /**
     * Получение списка всех устройств.
     *
     * @return список устройств в формате DataDeviceRequest
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Device> devices = deviceService.getAll();
            List<DataDeviceRequest> dataDevices = devices.stream()
                    .map(device -> new DataDeviceRequest(
                            device.getId(),
                            device.getUser().getId(),
                            device.getName(),
                            device.getMacAddress()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dataDevices);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    /**
     * Обновление данных устройства.
     *
     * @param id         ID устройства
     * @param userId     ID пользователя
     * @param name       Новое название устройства
     * @param macAddress Новый MAC-адрес устройства
     * @return ответ с обновленными данными устройства
     */
    @PutMapping
    public ResponseEntity<?> update(@RequestParam("id") Long id,
                                    @RequestParam("user_id") Long userId,
                                    @RequestParam("name") String name,
                                    @RequestParam("mac_address") String macAddress) {
        try {
            // Проверка на null или пустое значение mac_address
            if (macAddress == null || macAddress.isEmpty()) {
                // Возвращаем ошибку с конкретным сообщением
                throw new IllegalArgumentException("Параметр 'mac_address' обязателен");
            }

            // Создаем объект DataDeviceRequest для обновления устройства
            DataDeviceRequest deviceRequest = new DataDeviceRequest();
            deviceRequest.setId(id);
            deviceRequest.setUserId(userId);
            deviceRequest.setName(name);
            deviceRequest.setMacAddress(macAddress);

            // Обновляем устройство через сервис
            deviceService.update(deviceRequest);

            // Возвращаем ответ с обновленным устройством
            return ResponseEntity.ok(deviceRequest);
        } catch (Exception e) {
            // Передаем исключение в метод handleError
            return handleError(e);
        }
    }


    /**
     * Удаление устройства.
     *
     * @param id идентификатор устройства
     * @return ответ с сообщением об успешном удалении
     */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        try {
            // Удаляем устройство через сервис
            deviceService.delete(id);

            // Возвращаем успешный ответ
            return ResponseEntity.ok("Устройство удалено");
        } catch (Exception e) {
            // Обрабатываем ошибку и возвращаем ответ с сообщением об ошибке
            return ResponseEntity.internalServerError().body("Ошибка при удалении устройства: " + e.getMessage());
        }
    }


    /**
     * Универсальный метод для обработки ошибок.
     *
     * @param e исключение
     * @return ответ с сообщением об ошибке
     */
    private ResponseEntity<?> handleError(Exception e) {
        // Возвращаем ответ с ошибкой и сообщением из исключения
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
    }

}
