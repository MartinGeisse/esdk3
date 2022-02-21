/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal;

import name.martingeisse.esdk.core.library.clocked.Clock;

/**
 *
 */
public interface ClockSignal extends Signal {

	Clock getClock();

}
