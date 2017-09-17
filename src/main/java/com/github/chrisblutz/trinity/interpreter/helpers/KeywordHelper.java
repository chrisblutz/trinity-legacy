package com.github.chrisblutz.trinity.interpreter.helpers;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;


/**
 * @author Christopher Lutz
 */
public interface KeywordHelper {
    
    TYObject evaluate(TYObject thisObj, TokenInfo info, Location location, TYRuntime runtime);
}
