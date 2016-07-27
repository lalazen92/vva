/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.sentence.detection;

/**
 *
 * @author phiha
 */
public class Attribute {

    private String candidateType;
    private String prefixType;
    private String suffixType;
    private String nextWordType;
    private String previousWordType;
    private int prefixLength;

    public Attribute() {
    }

    public Attribute(String candidateType, String prefixType, String suffixType, String nextWordType, String previousWordType, int prefixLength) {
        this.candidateType = candidateType;
        this.prefixType = prefixType;
        this.suffixType = suffixType;
        this.nextWordType = nextWordType;
        this.previousWordType = previousWordType;
        this.prefixLength = prefixLength;
    }

    public String getCandidateType() {
        return candidateType;
    }

    public void setCandidateType(String candidateType) {
        this.candidateType = candidateType;
    }

    public String getPrefixType() {
        return prefixType;
    }

    public void setPrefixType(String prefixType) {
        this.prefixType = prefixType;
    }

    public String getSuffixType() {
        return suffixType;
    }

    public void setSuffixType(String suffixType) {
        this.suffixType = suffixType;
    }

    public String getNextWordType() {
        return nextWordType;
    }

    public void setNextWordType(String nextWordType) {
        this.nextWordType = nextWordType;
    }

    public String getPreviousWordType() {
        return previousWordType;
    }

    public void setPreviousWordType(String previousWordType) {
        this.previousWordType = previousWordType;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

}
