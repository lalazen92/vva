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
public class J48Classifier {

     public static String classify(Attribute attribute) {
        String punctuation = attribute.getCandidateType();
       
        String prefixType = attribute.getPrefixType();
        
        String suffixType = attribute.getSuffixType();
        String nextWordType = attribute.getNextWordType();
        String previousWordType = attribute.getPreviousWordType();
        int prefixLength = attribute.getPrefixLength();
        
        switch (suffixType) {
            case "empty":
                switch (punctuation) {
                    case "dot":
                        switch (nextWordType) {
                            case "empty":
                                return "yes";
                            case "upper":
                                if (prefixLength <= 2) {
                                    switch (prefixType) {
                                        case "empty":
                                            return "yes";
                                        case "upper":
                                            return "no";
                                        case "lower":
                                            return "yes";
                                        case "numeric":
                                            return "no";
                                        case "capword":
                                            return "no";
                                        case "misc":
                                            return "no";
                                    }
                                } else if (prefixLength > 2) {
                                    return "yes";
                                }
                                break;
                            case "lower":
                                return "no";
                            case "numeric":
                                if (prefixLength <= 2) {
                                    return "no";
                                } else if (prefixLength > 2) {
                                    return "yes";
                                }
                                break;
                            case "capword":
                                if (prefixLength <= 1) {
                                    switch (prefixType) {
                                        case "empty":
                                            switch (previousWordType) {
                                                case "empty":
                                                    return "no";
                                                case "upper":
                                                    return "yes";
                                                case "lower":
                                                    return "yes";
                                                case "numeric":
                                                    return "yes";
                                                case "capword":
                                                    return "yes";
                                                case "misc":
                                                    return "yes";
                                            }
                                            break;
                                        case "upper":
                                            switch (previousWordType) {
                                                case "empty":
                                                    return "no";
                                                case "upper":
                                                    return "no";
                                                case "lower":
                                                    return "yes";
                                                case "numeric":
                                                    return "no";
                                                case "capword":
                                                    return "no";
                                                case "misc":
                                                    return "no";
                                            }
                                            break;
                                        case "lower":
                                            return "yes";
                                        case "numeric":
                                            return "no";
                                        case "capword":
                                            return "yes";
                                        case "misc":
                                            return "yes";
                                    }
                                } else if (prefixLength > 1) {
                                    return "yes";
                                }
                                break;
                            case "misc":
                                return "yes";
                        }
                        break;
                    case "quest":
                        switch (nextWordType) {
                            case "empty":
                                return "yes";
                            case "upper":
                                return "no";
                            case "lower":
                                return "no";
                            case "numeric":
                                return "yes";
                            case "capword":
                                return "yes";
                            case "misc":
                                return "yes";
                        }
                        break;
                    case "exclam":
                        switch (nextWordType) {
                            case "empty":
                                return "yes";
                            case "upper":
                                switch (prefixType) {
                                    case "empty":
                                        return "no";
                                    case "upper":
                                        return "no";
                                    case "lower":
                                        return "yes";
                                    case "numeric":
                                        return "no";
                                    case "capword":
                                        return "no";
                                    case "misc":
                                        return "no";
                                }
                                break;
                            case "lower":
                                return "no";
                            case "numeric":
                                return "yes";
                            case "capword":
                                return "yes";
                            case "misc":
                                return "no";
                        }
                        break;
                    case "colon":
                        switch (nextWordType) {
                            case "empty":
                                return "yes";
                            case "upper":
                                return "no";
                            case "lower":
                                return "no";
                            case "numeric":
                                return "no";
                            case "capword":
                                return "no";
                            case "misc":
                                return "no";
                        }
                        break;
                }
                break;
            case "upper":
                return "no";
            case "lower":
                return "no";
            case "numeric":
                return "no";
            case "capword":
                switch(prefixType){
                    case Type.UPPER:
                        return "no";
                    default:
                        return "yes";
                }
            case "misc":
                return "no";
        }
        return null;
    }    
}
