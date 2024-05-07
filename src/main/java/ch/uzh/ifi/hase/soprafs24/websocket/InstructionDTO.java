package ch.uzh.ifi.hase.soprafs24.websocket;

import ch.uzh.ifi.hase.soprafs24.constant.Instruction;

public class InstructionDTO {

    private Instruction instruction;

    private String reason;

    public InstructionDTO() {}

    public InstructionDTO(Instruction instruction) {
        this.instruction = instruction;
    }

    public InstructionDTO(Instruction instruction, String reason) {
        this.instruction = instruction;
        this.reason = reason;
    }

    public Instruction getInstruction() {
        return instruction;
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
}
