package task2_intelligent_resource_allocation.service;

import task2_intelligent_resource_allocation.dto.AllocationResponseDTO;

public interface IntelligentResourceAllocationService {
    AllocationResponseDTO executeBatchAllocation();
    AllocationResponseDTO executeInstantAllocation();
}