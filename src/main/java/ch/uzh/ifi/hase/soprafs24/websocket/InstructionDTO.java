package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.constant.Instruction;

public class InstructionDTO {

    private Instruction instruction;

    private String reason;

    private Object data;

    public InstructionDTO() {}

    public InstructionDTO(Instruction instruction) {
        this.instruction = instruction;
    }

    public InstructionDTO(Instruction instruction, Object data) {
        this.instruction = instruction;
        this.data = data;
    }

    public InstructionDTO(Instruction instruction, Object data, String reason) {
        this.instruction = instruction;
        this.data = data;
        this.reason = reason;
    }

    public String getInstruction() {
        return instruction.toString();
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
