import tester.*;
import java.awt.Color;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.util.Random;

// represents the mastermind game
class Mastermind extends World {
  boolean duplicate;
  int length;
  int guesses;
  ILoColor sequence;
  ILoColor currentGuess;
  ILoColor allGuesses;
  ILoExactInexact exactInexact;
  int exact;
  int inexact;
  int numPresses;
  ILoColor solution;

  // starting game constructor for mastermind world
  Mastermind(boolean duplicate, int length, int guesses, ILoColor sequence) {
    this.duplicate = duplicate;
    this.length = new Utils().isValid(length, 0, "Invalid length: ");
    this.guesses = new Utils().isValid(guesses, 0, "Invalid guesses: ");
    this.sequence = new Utils().isValidListLength(sequence, sequence.length(), 0,
        "Invalid sequence length: ");
    if (!this.duplicate && this.length > this.sequence.length()) {
      throw new IllegalArgumentException("Sequence length is greater than list of colors.");
    }
    this.currentGuess = new MtLoColor();
    this.allGuesses = new MtLoColor();
    this.exactInexact = new MtLoExactInexact();
    this.exact = 0;
    this.inexact = 0;
    this.numPresses = 0;
    this.solution = this.createRandomSolution(this.length);
  }

  // key events constructor for mastermind world
  Mastermind(boolean duplicate, int length, int guesses, ILoColor sequence, ILoColor currentGuess,
      ILoColor allGuesses, ILoExactInexact exactInexact, int exact, int inexact, int numPresses,
      ILoColor solution) {
    this.duplicate = duplicate;
    this.length = length;
    this.guesses = guesses;
    this.sequence = sequence;
    this.currentGuess = currentGuess;
    this.allGuesses = allGuesses;
    this.exactInexact = exactInexact;
    this.exact = exact;
    this.inexact = inexact;
    this.numPresses = numPresses;
    this.solution = solution;
  }
  
  /* Template:
   * Fields:
   * ... this.duplicate ...         -- boolean
   * ... this.length ...            -- int
   * ... this.guesses ...           -- int
   * ... this.sequence ...          -- ILoColor
   * ... this.currentGuess ...      -- ILoColor
   * ... this.allGuesses ...        -- ILoColor
   * ... this.exactInexact ...      -- ILoExactInexact
   * ... this.exact ...             -- int
   * ... this.inexact ...           -- int
   * ... this.numPresses ...        -- int
   * ... this.solution ...          -- ILoColor
   * 
   * Methods on Fields:
   * ... this.sequence.length() ...                                              -- int
   * ... this.sequence.colorsToCircles() ...                                     -- ILoCircle
   * ... this.sequence.searchColor(int) ...                                      -- Color
   * ... this.sequence.deleteFirst() ...                                         -- ILoColor 
   * ... this.sequence.checkExact(ILoColor) ...                                  -- int
   * ... this.sequence.checkExactHelp(ILoColor, Color) ...                       -- int
   * ... this.sequence.checkInexact(ILoColor) ...                                -- int
   * ... this.sequence.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int
   * ... this.sequence.append(ILoColor) ...                                      -- ILoColor
   * ... this.currentGuess.length() ...                                              -- int
   * ... this.currentGuess.colorsToCircles() ...                                     -- ILoCircle
   * ... this.currentGuess.searchColor(int) ...                                      -- Color
   * ... this.currentGuess.deleteFirst() ...                                         -- ILoColor 
   * ... this.currentGuess.checkExact(ILoColor) ...                                  -- int
   * ... this.currentGuess.checkExactHelp(ILoColor, Color) ...                       -- int
   * ... this.currentGuess.checkInexact(ILoColor) ...                                -- int
   * ... this.currentGuess.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int
   * ... this.currentGuess.append(ILoColor) ...                                      -- ILoColor
   * ... this.allGuesses.length() ...                                              -- int
   * ... this.allGuesses.colorsToCircles() ...                                     -- ILoCircle
   * ... this.allGuesses.searchColor(int) ...                                      -- Color
   * ... this.allGuesses.deleteFirst() ...                                         -- ILoColor 
   * ... this.allGuesses.checkExact(ILoColor) ...                                  -- int
   * ... this.allGuesses.checkExactHelp(ILoColor, Color) ...                       -- int
   * ... this.allGuesses.checkInexact(ILoColor) ...                                -- int
   * ... this.allGuesses.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int
   * ... this.allGuesses.append(ILoColor) ...                                      -- ILoColor
   * ... this.exactInexact.drawExactInexact() ...           -- WorldImage
   * ... this.exactInexact.deleteFirst() ...                -- ILoExactInexact
   * ... this.solution.length() ...                                              -- int
   * ... this.solution.colorsToCircles() ...                                     -- ILoCircle
   * ... this.solution.searchColor(int) ...                                      -- Color
   * ... this.solution.deleteFirst() ...                                         -- ILoColor 
   * ... this.solution.checkExact(ILoColor) ...                                  -- int
   * ... this.solution.checkExactHelp(ILoColor, Color) ...                       -- int
   * ... this.solution.checkInexact(ILoColor) ...                                -- int
   * ... this.solution.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int
   * ... this.solution.append(ILoColor) ...                                      -- ILoColor
   * 
   * Methods:
   * ... this.makeScene() ...                                -- WorldScene
   * ... this.lastScene(String) ...                          -- WorldScene
   * ... this.drawAllEmptyCircles(int, int, WorldImage) ...  -- WorldImage
   * ... this.drawNEmptyCircles(int, WorldImage) ...         -- WorldImage
   * ... this.onkeyEvent(String) ...                         -- World
   * ... this.possibleNumbers(int) ...                       -- String
   * ... this.addCircle(int) ...                             -- Mastermind
   * ... this.deleteCircle() ...                             -- Mastermind
   * ... this.enterGuess() ...                               -- Mastermind
   * ... this.createRandomSolution(int) ...                  -- ILoColor
   */

  // draws this mastermind world
  public WorldScene makeScene() {
    WorldScene bkg = new WorldScene(500, 500);
    int currentGuesses = this.allGuesses.length() / this.length;

    WorldImage sequence = this.sequence.colorsToCircles().drawCircles();
    WorldImage allGuessesExactInexact = this.allGuesses.colorsToCircles()
        .drawGuessesAndExactInexacts(this.length, currentGuesses, new EmptyImage(),
            this.exactInexact);
    WorldImage addSequenceAndAllGuesses = new AboveAlignImage("left", allGuessesExactInexact,
        sequence);
    WorldImage addCurrentGuess = new AboveAlignImage("left",
        this.currentGuess.colorsToCircles().drawCirclesReverse(), addSequenceAndAllGuesses);
    WorldImage addEmptyGuesses = new AboveAlignImage("left", new AboveAlignImage("left",
        this.drawAllEmptyCircles(this.length, this.guesses, new EmptyImage()), addCurrentGuess));
    WorldImage addHiddenSolution = new AboveAlignImage("left",
        new RectangleImage(this.length * 20, 20, "solid", Color.black), addEmptyGuesses);
    WorldScene all = bkg.placeImageXY(addHiddenSolution, 250, 250);
    return all;
  }

  // draws this last mastermind world scene
  public WorldScene lastScene(String msg) {
    WorldScene bkg = new WorldScene(500, 500);
    int currentGuesses = this.allGuesses.length() / this.length;

    WorldImage sequence = this.sequence.colorsToCircles().drawCirclesReverse();
    WorldImage allGuessesExactInexact = this.allGuesses.colorsToCircles()
        .drawGuessesAndExactInexacts(this.length, currentGuesses, new EmptyImage(),
            this.exactInexact);
    WorldImage addSequenceAndAllGuesses = new AboveAlignImage("center", allGuessesExactInexact,
        sequence);
    WorldImage addCurrentGuess = new AboveAlignImage("left",
        this.currentGuess.colorsToCircles().drawCircles(), addSequenceAndAllGuesses);
    WorldImage addEmptyGuesses = new AboveAlignImage("left", new AboveAlignImage("left",
        this.drawAllEmptyCircles(this.length, this.guesses, new EmptyImage()), addCurrentGuess));
    WorldImage addSolution = new AboveAlignImage("left",
        new OverlayImage(this.solution.colorsToCircles().drawCircles(),
            new RectangleImage(this.length * 20, 20, "solid", Color.black)),
        addEmptyGuesses);
    WorldImage winner = new AboveImage(new TextImage(msg, 20, Color.black), addSolution);
    WorldScene all = bkg.placeImageXY(winner, 250, 250);
    return all;
  }

  // draws the given number of circles above the given length, representing this world's
  // total number of guesses
  public WorldImage drawAllEmptyCircles(int length, int count, WorldImage curr) {
    if (count == 0) {
      return curr;
    } else {
      return new AboveAlignImage("left", this.drawNEmptyCircles(length, curr),
          this.drawAllEmptyCircles(length, count - 1, curr));
    }
  }

  // draws the given number of empty circles beside each other, representing this world's
  // empty guesses in a row
  public WorldImage drawNEmptyCircles(int length, WorldImage curr) {
    if (length == 0) {
      return curr;
    } else {
      return new BesideImage(new CircleImage(10, "outline", Color.black),
          this.drawNEmptyCircles(length - 1, curr));
    }
  }

  // controls the key inputs and creates a new changed world accordingly
  // a number adds a circle as part of the user's guess depending on this world's sequence
  // backspace deletes the circle of the current guess of this world
  // enter submits the full current guess, either continuing the game with the number of
  // exacts and inexacts shown or ends the world when the user wins/loses
  // else returns this same world
  public World onKeyEvent(String key) {
    String num = this.possibleNumbers(this.sequence.length());

    if (this.numPresses > this.length) {
      return this;
    } 
    else if (key.equals("enter") && this.numPresses == this.length) {
      this.exact = this.currentGuess.checkExact(this.solution);
      this.inexact = this.currentGuess.checkInexact(this.solution) - this.exact;
      int currentGuesses = this.allGuesses.length() / this.length;

      if (this.exact == this.length) {
        return this.endOfWorld("You win! It took you " + currentGuesses + " guess(es).");
      } else if (this.guesses == 0) {
        return this.endOfWorld("You lose!");
      } else {
        return this.enterGuess();
      }
    }
    else if (num.contains(key)) {

      if (this.numPresses < this.length) {
        this.numPresses++;
        if (this.numPresses == 1) {
          this.guesses--;
        }
        return this.addCircle(Integer.valueOf(key));
      } else {
        return this;
      }
    }
    else if (key.equals("backspace")) {

      if (this.numPresses == 0) {
        return this;
      } else {
        if (this.numPresses == 1) {
          this.guesses++;
        }
        this.numPresses--;
        return this.deleteCircle();
      }
    }
    else {
      return this;
    }
  }

  // creates a string of numbers descending to 1 starting from this game's sequence length
  public String possibleNumbers(int length) {
    if (length == 0) {
      return "";
    } else {
      return this.possibleNumbers(length - 1) + Integer.toString(length);
    }
  }

  // creates a new mastermind with a new colored circle drawn as the guess
  public Mastermind addCircle(int numColor) {
    Color circleColor = this.sequence.searchColor(numColor);
    return new Mastermind(this.duplicate, this.length, this.guesses, this.sequence,
        new ConsLoColor(circleColor, this.currentGuess), this.allGuesses, this.exactInexact,
        this.exact, this.inexact, this.numPresses, this.solution);
  }

  // creates a new mastermind with the last colored circle guessed deleted
  public Mastermind deleteCircle() {
    return new Mastermind(this.duplicate, this.length, this.guesses, this.sequence,
        this.currentGuess.deleteFirst(), this.allGuesses, this.exactInexact, this.exact,
        this.inexact, this.numPresses, this.solution);
  }

  // creates a new mastermind revealing the number of exact and inexact matches of last guess
  // and replacing the current guess with an empty list
  public Mastermind enterGuess() {
    return new Mastermind(this.duplicate, this.length, this.guesses, this.sequence, new MtLoColor(),
        this.currentGuess.append(this.allGuesses),
        new ConsLoExactInexact(exact + " " + inexact, this.exactInexact), this.exact, this.inexact,
        0, this.solution);
  }

  // generates a list of random colors from the list of colors in this mastermind
  public ILoColor createRandomSolution(int length) {
    Random r = new Random();
    if (length == 0) {
      return new MtLoColor();
    } else {
      return new ConsLoColor(this.sequence.searchColor(r.nextInt(this.sequence.length()) + 1),
          this.createRandomSolution(length - 1));
    }
  }
}

// represents a circle
class Circle {
  Color color;
  String type;

  // the constructor
  Circle(Color color, String type) {
    this.color = color;
    this.type = type;
  }

  /*
   * Template: Fields: ... this.color ... -- Color ... this.type ... -- String
   * 
   * Methods: ... this.drawCircle() ... -- WorldImage
   * 
   */

  // draws a Circle
  public WorldImage drawCircle() {
    return new CircleImage(10, type, this.color);
  }
}

// represent a list of circles
interface ILoCircle {

  // draws an ILoCircle
  WorldImage drawCircles();

  // draws an ILoCircle in reverse
  WorldImage drawCirclesReverse();

  // draws an ILoCircle besides an ILoExactInexact
  WorldImage drawGuessesAndExactInexacts(int length, int count, WorldImage curr,
      ILoExactInexact exactInexact);

  // draws an ILoCircle of length _length_
  WorldImage drawNCircles(int length, WorldImage curr);

  // deletes the first _count_ circles of an ILoCircle
  ILoCircle deleteNCircles(int count);
}

// represent an empty list of circles
class MtLoCircle implements ILoCircle {

  // returns an EmptyImage
  public WorldImage drawCircles() {
    return new EmptyImage();
  }

  // returns an EmptyImage
  public WorldImage drawCirclesReverse() {
    return new EmptyImage();
  }

  // returns the current image
  public WorldImage drawGuessesAndExactInexacts(int length, int count, WorldImage curr,
      ILoExactInexact exactInexact) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Fields of Parameters: 
     * ... exactInexact.first ... -- String 
     * ... exactInexact.rest ... -- ILoExactInexact
     * 
     * Methods of Parameters: 
     * ... exactInexact.drawExactInexact() ... -- WorldImage
     * ... exactInexact.deleteFirst() ... -- ILoExactInexact
     */
    return curr;
  }

  // returns the current image
  public WorldImage drawNCircles(int length, WorldImage curr) {
    return curr;
  }

  // returns an MtLoCircle
  public ILoCircle deleteNCircles(int count) {
    return this;
  }
}

// represents a list of circles
class ConsLoCircle implements ILoCircle {
  Circle first;
  ILoCircle rest;

  // the constructor
  ConsLoCircle(Circle first, ILoCircle rest) {
    this.first = first;
    this.rest = rest;
  }

  // draws a ConsLoCircle in reverse
  public WorldImage drawCircles() {
    return new BesideImage(this.first.drawCircle(), this.rest.drawCircles());
  }

  // draws a ConsLoCircle in reverse
  public WorldImage drawCirclesReverse() {
    return new BesideImage(this.rest.drawCirclesReverse(), this.first.drawCircle());
  }

  // draws a ConsLoCircle besides an ILoExactInexact
  public WorldImage drawGuessesAndExactInexacts(int length, int count, WorldImage curr,
      ILoExactInexact exactInexact) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Fields of Parameters: 
     * ... exactInexact.first ... -- String 
     * ... exactInexact.rest ... -- ILoExactInexact
     * 
     * Methods of Parameters: 
     * ... exactInexact.drawExactInexact() ... -- WorldImage
     * ... exactInexact.deleteFirst() ... -- ILoExactInexact
     */
    if (count == 0) {
      return curr;
    }
    else {
      return new AboveAlignImage("left",
          new BesideImage(this.drawNCircles(length, curr), exactInexact.drawExactInexact()),
          this.deleteNCircles(length).drawGuessesAndExactInexacts(length, count - 1, curr,
              exactInexact.deleteFirst()));
    }
  }

  // draws a ConsLoCircle of length _length_
  public WorldImage drawNCircles(int length, WorldImage curr) {
    if (length == 0) {
      return curr;
    }
    else {
      return new BesideImage(this.rest.drawNCircles(length - 1, curr), this.first.drawCircle());
    }
  }

  // deletes the first _count_ circles of a ConsLoCircle
  public ILoCircle deleteNCircles(int count) {
    if (count == 0) {
      return this;
    }
    else {
      return this.rest.deleteNCircles(count - 1);
    }
  }
}

// represents a list of colors
interface ILoColor {

  // counts the number of colors in this list of colors
  int length();

  // creates a list of circles of the colors from this list of colors
  ILoCircle colorsToCircles();

  // returns the color at the given index number in this list of colors
  Color searchColor(int numColor);

  // deletes the first color in this list of colors
  ILoColor deleteFirst();

  // checks the number of matching colors at the same index from the given list
  // and this list
  int checkExact(ILoColor that);

  // helper to checkExact, keeps track of the given list of colors and compares
  // the given color with the first element of this list of colors
  int checkExactHelp(ILoColor that, Color color);

  // checks the number of matching colors from the given list and this list
  int checkInexact(ILoColor that);

  // helper to checkInexact, keeps track of the given list of colors and compares
  // the given color with the given list of colors
  int checkInexactHelp(ILoColor that, Color color, ILoColor solution, ILoColor acc);

  // given this list of colors and that list of colors, returns a new
  // list of colors that appends the two
  ILoColor append(ILoColor that);
}

// represents an empty list of colors
class MtLoColor implements ILoColor {

  /*
   * Template: Fields:
   * 
   * Methods: 
   * ... this.lengthOfList() ... -- int 
   * ... this.makeCircles() ... -- ILoCircle 
   * ... this.searchColor(int) ... -- Color 
   * ... this.deleteFirst() ... -- ILoColor 
   * ... this.checkExact() ... -- int 
   * ... this.checkExactHelp() ... -- int
   * 
   * Methods of Fields:
   * 
   */

  // returns 0
  public int length() {
    return 0;
  }

  // returns an MtLoCircle
  public ILoCircle colorsToCircles() {
    return new MtLoCircle();
  }

  // returns null
  public Color searchColor(int numColor) {
    return null;
  }

  // returns an MtLoColor
  public ILoColor deleteFirst() {
    return this;
  }

  // returns 0
  public int checkExact(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return 0;
  }

  // returns 0
  public int checkExactHelp(ILoColor that, Color color) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return 0;
  }

  // returns 0
  public int checkInexact(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return 0;
  }

  // returns 0
  public int checkInexactHelp(ILoColor that, Color color, ILoColor solution, ILoColor acc) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters:
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     * 
     * ... solution.length() ... -- int 
     * ... solution.colorsToCircles() ... -- ILoCircle 
     * ... solution.searchColor(int) ... -- Color 
     * ... solution.deleteFirst() ... -- ILoColor 
     * ... solution.checkExact(ILoColor) ... -- int 
     * ... solution.checkExactHelp(ILoColor, Color) ... -- int 
     * ... solution.checkInexact(ILoColor) ... -- int 
     * ... solution.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... solution.append(ILoColor) ... -- ILoColor
     *
     * 
     * ... acc.length() ... -- int 
     * ... acc.colorsToCircles() ... -- ILoCircle 
     * ... acc.searchColor(int) ... -- Color 
     * ... acc.deleteFirst() ... -- ILoColor 
     * ... acc.checkExact(ILoColor) ... -- int 
     * ... acc.checkExactHelp(ILoColor, Color) ... -- int 
     * ... acc.checkInexact(ILoColor) ... -- int 
     * ... acc.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... acc.append(ILoColor) ... -- ILoColor
     */
    return that.checkInexact(solution);
  }

  // returns that list of colors
  public ILoColor append(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return that;
  }
}

// represents a nonempty list of colors
class ConsLoColor implements ILoColor {
  Color first;
  ILoColor rest;

  // the constructor
  ConsLoColor(Color first, ILoColor rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * Template: 
   * Fields: 
   * ... this.first ... -- Color 
   * ... this.rest ... -- ILoColor
   *
   * Methods: 
   * ... this.lengthOfList() ... -- int 
   * ... this.makeCircles() ... -- ILoCircle 
   * ... this.searchColor(int) ... -- Color 
   * ... this.deleteFirst() ... -- ILoColor 
   * ... this.checkExact() ... -- int 
   * ... this.checkExactHelp() ... -- int
   *
   * Methods on Fields: 
   * ... this.rest.lengthOfList() ... -- int 
   * ... this.rest.makeCircles() ... -- ILoCircle 
   * ... this.rest.searchColor(int) ... -- Color 
   * ... this.rest.deleteFirst() ... -- ILoColor 
   * ... this.rest.checkExact(ILoColor) ... -- int 
   * ... this.rest.checkExactHelp(ILoColor, Color) ... -- int
   * 
   */

  // counts the number of colors in this ConsLoColor
  public int length() {
    return 1 + this.rest.length();
  }

  // creates a list of circles of the colors from this ConsLoColor
  public ILoCircle colorsToCircles() {
    return new ConsLoCircle(new Circle(this.first, "solid"), this.rest.colorsToCircles());
  }

  // returns the color at the given index number in this ConsLoColor
  public Color searchColor(int numColor) {
    if (numColor == 1) {
      return this.first;
    }
    else {
      return this.rest.searchColor(numColor - 1);
    }
  }

  // deletes the first color in this ConsLoColor
  public ILoColor deleteFirst() {
    return this.rest;
  }

  // checks the number of matching colors at the same index from the given
  // list of colors and this ConsLoColor
  public int checkExact(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return that.checkExactHelp(this.rest, this.first);
  }

  // helper to checkExact, keeps track of the given list of colors and compares
  // the given color with the first element of this ConsLoColor
  public int checkExactHelp(ILoColor that, Color color) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    if (this.first.equals(color)) {
      return 1 + this.rest.checkExact(that);
    }
    else {
      return this.rest.checkExact(that);
    }
  }

  // checks the number of matching colors from the given list and ConsLoColor
  public int checkInexact(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return that.checkInexactHelp(this.rest, this.first, that, new MtLoColor());
  }

  // helper to checkInexact, keeps track of the given list of colors and compares
  // the given color with this ConsLoColor
  public int checkInexactHelp(ILoColor that, Color color, ILoColor solution, ILoColor acc) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters:
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     * 
     * ... solution.length() ... -- int 
     * ... solution.colorsToCircles() ... -- ILoCircle 
     * ... solution.searchColor(int) ... -- Color 
     * ... solution.deleteFirst() ... -- ILoColor 
     * ... solution.checkExact(ILoColor) ... -- int 
     * ... solution.checkExactHelp(ILoColor, Color) ... -- int 
     * ... solution.checkInexact(ILoColor) ... -- int 
     * ... solution.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... solution.append(ILoColor) ... -- ILoColor
     *
     * 
     * ... acc.length() ... -- int 
     * ... acc.colorsToCircles() ... -- ILoCircle 
     * ... acc.searchColor(int) ... -- Color 
     * ... acc.deleteFirst() ... -- ILoColor 
     * ... acc.checkExact(ILoColor) ... -- int 
     * ... acc.checkExactHelp(ILoColor, Color) ... -- int 
     * ... acc.checkInexact(ILoColor) ... -- int 
     * ... acc.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... acc.append(ILoColor) ... -- ILoColor
     */

    if (this.first.equals(color)) {
      return 1 + that.checkInexact(this.rest.append(acc));
    }
    else {
      return this.rest.checkInexactHelp(that, color, solution, new ConsLoColor(this.first, acc));
    }
  }

  // given this ConsLoColor and that list of colors, returns a new
  // list of colors that appends the two
  public ILoColor append(ILoColor that) {
    /*
     * Template: Everything from the class-wide template and...
     * 
     * Methods of Parameters: 
     * ... that.length() ... -- int 
     * ... that.colorsToCircles() ... -- ILoCircle 
     * ... that.searchColor(int) ... -- Color 
     * ... that.deleteFirst() ... -- ILoColor 
     * ... that.checkExact(ILoColor) ... -- int 
     * ... that.checkExactHelp(ILoColor, Color) ... -- int 
     * ... that.checkInexact(ILoColor) ... -- int 
     * ... that.checkInexactHelp(ILoColor, Color, ILoColor, ILoColor) ... -- int 
     * ... that.append(ILoColor) ... -- ILoColor
     */
    return new ConsLoColor(this.first, this.rest.append(that));
  }
}

// represents a list of exacts and inexacts of the mastermind game as strings
interface ILoExactInexact {

  // draws the first element of this list
  WorldImage drawExactInexact();

  // deletes the first element of this list
  ILoExactInexact deleteFirst();
}

// represents an empty list of exacts and inexacts as a string
class MtLoExactInexact implements ILoExactInexact {
  
  /* Template:
   * Method:
   * ... this.drawExactInexact() ...    -- WorldImage
   * ... this.deleteFirst() ...         -- ILoExactInexact
   */

  // draws this empty list as an empty image
  public WorldImage drawExactInexact() {
    return new EmptyImage();
  }

  // deletes the first element of this list, returning this empty list
  public ILoExactInexact deleteFirst() {
    return this;
  }
}

// represents a nonempty list of exact and inexact as a single string
class ConsLoExactInexact implements ILoExactInexact {
  String first;
  ILoExactInexact rest;

  ConsLoExactInexact(String first, ILoExactInexact rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* Template:
   * Fields:
   * ... this.first ...         -- String
   * ... this.rest ...          -- ILoExactInexact
   * 
   * Method:
   * ... this.drawExactInexact() ...    -- WorldImage
   * ... this.deleteFirst() ...         -- ILoExactInexact
   */

  // draws the first element of this list
  public WorldImage drawExactInexact() {
    return new TextImage(this.first, 19, Color.black);
  }

  // returns the rest of this list of exact and inexact
  public ILoExactInexact deleteFirst() {
    return this.rest;
  }
}

// contains utility methods with no referenced object
class Utils {

  /*
   * Template: 
   * Methods: 
   * ... this.checkRange(int, int, int, String) ...                -- int
   * ... this.isValidListLength(ILoColor, int, int, String) ...    -- ILoColor
   */

  // confirms that given value above the given minimum else errors
  int isValid(int val, int min, String msg) {
    if (val > min) {
      return val;
    } else {
      throw new IllegalArgumentException(msg + val);
    }
  }

  // confirms that the length of the given list is above the given minimum else errors
  ILoColor isValidListLength(ILoColor val, int length, int min, String msg) {
    if (length > min) {
      return val;
    } else {
      throw new IllegalArgumentException(msg + length);
    }
  }
}

// represents examples and tests for mastermind and the related classes
class ExamplesMastermind {

  ILoColor sequence = new ConsLoColor(Color.cyan, new ConsLoColor(Color.pink,
      new ConsLoColor(Color.blue, new ConsLoColor(Color.green, new MtLoColor()))));
  ILoColor sequence2 = new ConsLoColor(Color.cyan, new ConsLoColor(Color.pink, 
      new ConsLoColor(Color.blue, new ConsLoColor(Color.green,
          new ConsLoColor(Color.red, new ConsLoColor(Color.gray, new MtLoColor()))))));
  
  ILoColor mtcolor = new MtLoColor();
  ILoColor guess = new ConsLoColor(Color.pink, new ConsLoColor(Color.green,
      new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor()))));
  ILoColor guess2 = new ConsLoColor(Color.pink, new ConsLoColor(Color.pink,
      new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor()))));
  ILoColor guess3 = new ConsLoColor(Color.pink, new ConsLoColor(Color.blue,
      new ConsLoColor(Color.green, new ConsLoColor(Color.pink, new MtLoColor()))));

  ILoColor solution = new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan,
      new ConsLoColor(Color.green, new ConsLoColor(Color.pink, new MtLoColor()))));
  ILoCircle circleGuess = new ConsLoCircle(new Circle(Color.pink, "solid"),
      new ConsLoCircle(new Circle(Color.green, "solid"),
          new ConsLoCircle(new Circle(Color.blue, "solid"),
              new ConsLoCircle(new Circle(Color.cyan, "solid"), new MtLoCircle()))));

  ILoCircle circleGuess2 = new ConsLoCircle(new Circle(Color.pink, "solid"),
      new ConsLoCircle(new Circle(Color.pink, "solid"),
          new ConsLoCircle(new Circle(Color.blue, "solid"),
              new ConsLoCircle(new Circle(Color.cyan, "solid"), new MtLoCircle()))));
  
  ILoExactInexact mtIE = new MtLoExactInexact();

  ILoExactInexact exactInexactExample = new ConsLoExactInexact("1 2",
      (new ConsLoExactInexact("3 2", mtIE)));
  
  Mastermind example = new Mastermind(true, 3, 10, this.sequence);
  Mastermind example2 = new Mastermind(true, 4, 15, this.sequence2);
  Mastermind example3 = new Mastermind(true, 3, 10, this.sequence,
      new MtLoColor(), new MtLoColor(), new MtLoExactInexact(), 0, 0, 0, this.solution);
  Mastermind example4 = new Mastermind(true, 2, 5, new ConsLoColor(Color.pink, 
      new ConsLoColor(Color.blue, new ConsLoColor(Color.red, new MtLoColor()))));
  Mastermind oneCircle = new Mastermind(true, 3, 9, this.sequence,
      new ConsLoColor(Color.cyan, new MtLoColor()), new MtLoColor(), 
      new MtLoExactInexact(), 0, 0, 1, this.solution);
  Mastermind oneGuess = new Mastermind(true, 3, 9, this.sequence,
      this.guess, new MtLoColor(), new MtLoExactInexact(), 0, 4, 3, this.solution);
  Mastermind twoGuess = new Mastermind(true, 3, 8, this.sequence,
      this.guess2, this.guess, new ConsLoExactInexact("0 4", new MtLoExactInexact()), 
      0, 3, 3, this.solution);
  Mastermind win = new Mastermind(true, 3, 10, this.sequence, this.solution, 
      new MtLoColor(), new MtLoExactInexact(), 0, 0, 3, 
      new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan,
          new ConsLoColor(Color.green, new ConsLoColor(Color.pink, new MtLoColor())))));
  Mastermind lose = new Mastermind(true, 3, 1, this.sequence,
      this.guess, new MtLoColor(), new MtLoExactInexact(), 0, 0, 3, 
      this.solution);
  
  // test for bigbang
  boolean testBigBang(Tester t) {
    int worldWidth = 500;
    int worldHeight = 500;
    double tickRate = 2;
    return this.example.bigBang(worldWidth, worldHeight, tickRate);
  }

  // test the the mastermind class constructors
  boolean testBadMastermind(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Invalid length: 0"),
        "Mastermind", true, 0, 3, new ConsLoColor(Color.red, new MtLoColor()))
        && t.checkConstructorException(new IllegalArgumentException("Invalid guesses: 0"),
            "Mastermind", true, 1, 0, new ConsLoColor(Color.red, new MtLoColor()))
        && t.checkConstructorException(new IllegalArgumentException("Invalid sequence length: 0"),
            "Mastermind", true, 1, 3, new MtLoColor())
        && t.checkConstructorException(
            new IllegalArgumentException("Sequence length is " + "greater than list of colors."),
            "Mastermind", false, 6, 3, new ConsLoColor(Color.red, new MtLoColor()));
  }
  
  // test the makeScene method for a mastermind world
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.example.makeScene(), 
        new WorldScene(500, 500).placeImageXY(new AboveAlignImage("left",
            new RectangleImage(3 * 20, 20, "solid", Color.black), 
            new AboveAlignImage("left", new AboveAlignImage("left",
                this.example.drawAllEmptyCircles(3, 10, new EmptyImage()), 
                new AboveAlignImage("left", 
                    new MtLoColor().colorsToCircles().drawCircles(), new AboveAlignImage("left", 
                        new MtLoColor().colorsToCircles().drawGuessesAndExactInexacts(3, 0, 
                            new EmptyImage(), new MtLoExactInexact()),
                        this.sequence.colorsToCircles().drawCircles()))))), 250, 250))
        && t.checkExpect(this.example2.makeScene(), 
            new WorldScene(500, 500).placeImageXY(new AboveAlignImage("left",
                new RectangleImage(4 * 20, 20, "solid", Color.black),
                new AboveAlignImage("left", new AboveAlignImage("left",
                    this.example2.drawAllEmptyCircles(4, 15, new EmptyImage()), 
                    new AboveAlignImage(
                        "left", new MtLoColor().colorsToCircles().drawCircles(), 
                        new AboveAlignImage("left",
                            new MtLoColor().colorsToCircles().drawGuessesAndExactInexacts(4,
                                0, new EmptyImage(), new MtLoExactInexact()),
                            this.sequence2.colorsToCircles().drawCircles()))))), 250, 250));
  }
  
  // test the lastScene method for a mastermind world
  boolean testLastScene(Tester t) {
    return t.checkExpect(this.win.lastScene("You win!"), 
        new WorldScene(500, 500).placeImageXY(new AboveImage(
            new TextImage("You win!", 20, Color.black), new AboveAlignImage("left",
            new OverlayImage(this.solution.colorsToCircles().drawCircles(),
                new RectangleImage(3 * 20, 20, "solid", Color.black)),
            new AboveAlignImage("left", new AboveAlignImage("left",
                this.win.drawAllEmptyCircles(3, 10, new EmptyImage()), new AboveAlignImage("left",
                    this.solution.colorsToCircles().drawCircles(), 
                    new AboveAlignImage("center", new MtLoColor().colorsToCircles()
                        .drawGuessesAndExactInexacts(3, 0, new EmptyImage(),
                            new MtLoExactInexact()),
                    this.sequence.colorsToCircles().drawCirclesReverse())))))), 250, 250))
        && t.checkExpect(this.lose.lastScene("You lose!"),
            new WorldScene(500, 500).placeImageXY(new AboveImage(
                new TextImage("You lose!", 20, Color.black), new AboveAlignImage("left",
                new OverlayImage(this.solution.colorsToCircles().drawCircles(),
                    new RectangleImage(3 * 20, 20, "solid", Color.black)),
                new AboveAlignImage("left", new AboveAlignImage("left",
                    this.lose.drawAllEmptyCircles(3, 1, 
                        new EmptyImage()), new AboveAlignImage("left",
                        this.guess.colorsToCircles().drawCircles(), 
                        new AboveAlignImage("center", new MtLoColor().colorsToCircles()
                            .drawGuessesAndExactInexacts(3, 0, new EmptyImage(),
                                new MtLoExactInexact()),
                        this.sequence.colorsToCircles().drawCirclesReverse())))))), 250, 250));
  }

  
  // test the drawAllEmptyCircles method for a mastermind
  boolean testDrawAllEmptyCircles(Tester t) {
    return t.checkExpect(this.example.drawAllEmptyCircles(2, 3, new EmptyImage()),
        new AboveAlignImage("left", new BesideImage(new CircleImage(10, "outline", Color.black),
            new BesideImage(new CircleImage(10, "outline", Color.black), new EmptyImage())), 
            new AboveAlignImage("left", new BesideImage(new CircleImage(10, "outline", Color.black),
            new BesideImage(new CircleImage(10, "outline", Color.black), new EmptyImage())), 
                new AboveAlignImage("left", new BesideImage(new CircleImage(10, 
                    "outline", Color.black), new BesideImage(new CircleImage(10, "outline", 
                        Color.black))), new EmptyImage()))))
        && t.checkExpect(this.example.drawAllEmptyCircles(1, 2, new EmptyImage()),
            new AboveAlignImage("left", 
                new BesideImage(new CircleImage(10, "outline", Color.black), 
                new EmptyImage()), new AboveAlignImage("left", new BesideImage(new CircleImage(10, 
                    "outline", Color.black), new EmptyImage()))));
  }
  
  // test the drawNEmptyCircles method for a mastermind
  boolean testDrawNEmptyCircles(Tester t) {
    return t.checkExpect(this.example.drawNEmptyCircles(0, new EmptyImage()), 
        new EmptyImage())
        && t.checkExpect(this.example.drawNEmptyCircles(2, new EmptyImage()), 
            new BesideImage(new CircleImage(10, "outline", Color.black), 
                new BesideImage(new CircleImage(10, "outline", Color.black), new EmptyImage())));
  }
  
  // test the onKeyEvent method for a mastermind
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.example3.onKeyEvent("1"), 
        new Mastermind(true, 3, 9, this.sequence,
            new ConsLoColor(Color.cyan, new MtLoColor()), new MtLoColor(), new MtLoExactInexact(),
            0, 0, 1, this.solution))
        && t.checkExpect(this.example.onKeyEvent("backspace"), this.example)
        && t.checkExpect(this.example.onKeyEvent("enter"), this.example)
        && t.checkExpect(this.oneGuess.onKeyEvent("3"), this.oneGuess)
        && t.checkExpect(this.oneGuess.onKeyEvent("enter"), 
            new Mastermind(true, 3, 9, this.sequence, new MtLoColor(), this.guess,
                new ConsLoExactInexact(0 + " " + 4, 
                    new MtLoExactInexact()), 0, 4, 0, this.solution))
        && t.checkExpect(this.oneCircle.onKeyEvent("backspace"), 
            new Mastermind(true, 3, 10, this.sequence, 
                new MtLoColor(), new MtLoColor(), new MtLoExactInexact(),
                0, 0, 0, this.solution))
        && t.checkExpect(this.win.onKeyEvent("enter"), 
            new Mastermind(true, 3, 10, this.sequence, new MtLoColor(), this.solution,
                new ConsLoExactInexact("4 0", new MtLoExactInexact()), 4, 0, 0,
                (new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new ConsLoColor(
                    Color.green, new ConsLoColor(Color.pink, new MtLoColor())))))));
  }
  
  // test for possibleNumbers method for mastermind
  boolean testPossibleNumbers(Tester t) {
    return t.checkExpect(this.example.possibleNumbers(0), "")
        && t.checkExpect(this.example2.possibleNumbers(2), "12")
        && t.checkExpect(this.example.possibleNumbers(4), "1234");
  }

  // test the addCircle method for mastermind
  boolean testAddCircle(Tester t) {
    return t.checkOneOf(this.example3.addCircle(1), 
        new Mastermind(true, 3, 10, this.sequence,
            new ConsLoColor(Color.cyan, new MtLoColor()), new MtLoColor(), new MtLoExactInexact(),
            0, 0, 0, this.solution),
        new Mastermind(true, 3, 9, this.sequence,
            new ConsLoColor(Color.cyan, new MtLoColor()), new MtLoColor(), new MtLoExactInexact(),
            0, 0, 1, this.solution))
        && t.checkOneOf(this.oneCircle.addCircle(3), 
            new Mastermind(true, 3, 9, this.sequence,
                new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor())), 
                new MtLoColor(), new MtLoExactInexact(),
                0, 0, 1, this.solution),
            new Mastermind(true, 3, 10, this.sequence,
                new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor())), 
                new MtLoColor(), new MtLoExactInexact(),
                0, 0, 1, this.solution));
  }

  // test the deleteCircle method for mastermind
  boolean testDeleteCircle(Tester t) {
    return t.checkExpect(this.example.deleteCircle(), this.example)
        && t.checkOneOf(this.oneCircle.deleteCircle(), 
            new Mastermind(true, 3, 10, this.sequence, new MtLoColor(), new MtLoColor(), 
                new MtLoExactInexact(), 0, 0, 0, this.solution), 
            new Mastermind(true, 3, 9, this.sequence, new MtLoColor(), new MtLoColor(), 
                new MtLoExactInexact(), 0, 0, 1, this.solution));
  }

  // test the enterGuess method for mastermind
  boolean testEnterGuess(Tester t) {
    return t.checkExpect(this.oneGuess.enterGuess(), 
            new Mastermind(true, 3, 9, this.sequence, new MtLoColor(), this.guess,
                new ConsLoExactInexact(0 + " " + 4, new MtLoExactInexact()), 
                0, 4, 0, this.solution))
        && t.checkExpect(this.twoGuess.enterGuess(), 
            new Mastermind(true, 3, 8, this.sequence, new MtLoColor(), 
                this.guess2.append(this.guess), 
                new ConsLoExactInexact(0 + " " + 3, 
                    new ConsLoExactInexact("0 4", 
                        new MtLoExactInexact())), 0, 3, 0, this.solution));
  }
  
  // test the createRandomSolution method for a mastermind
  boolean testCreateRandomSolution(Tester t) {
    return t.checkOneOf(this.example.createRandomSolution(1), 
        new ConsLoColor(Color.cyan, 
        new MtLoColor()), new ConsLoColor(Color.pink, new MtLoColor()), 
        new ConsLoColor(Color.green, 
                new MtLoColor()), new ConsLoColor(Color.blue, new MtLoColor()))
        && t.checkOneOf(this.example4.createRandomSolution(2), 
            new ConsLoColor(Color.pink, new ConsLoColor(Color.blue, new MtLoColor())), 
            new ConsLoColor(Color.blue, new ConsLoColor(Color.pink, new MtLoColor())),
            new ConsLoColor(Color.pink, new ConsLoColor(Color.red, new MtLoColor())),
            new ConsLoColor(Color.red, new ConsLoColor(Color.pink, new MtLoColor())),
            new ConsLoColor(Color.blue, new ConsLoColor(Color.red, new MtLoColor())),
            new ConsLoColor(Color.red, new ConsLoColor(Color.blue, new MtLoColor())),
            new ConsLoColor(Color.red, new ConsLoColor(Color.red, new MtLoColor())),
            new ConsLoColor(Color.blue, new ConsLoColor(Color.blue, new MtLoColor())),
            new ConsLoColor(Color.pink, new ConsLoColor(Color.pink, new MtLoColor())));
  }
  
  // test for draw method for a circle
  boolean testDrawCircle(Tester t) {
    return t.checkExpect(new Circle(Color.blue, "solid").drawCircle(),
        new CircleImage(10, "solid", Color.blue))
        && t.checkExpect(new Circle(Color.black, "outline").drawCircle(),
            new CircleImage(10, "outline", Color.black));
  }

  // tests the drawCircles method for classes that implement ILoCircle
  boolean testDrawCircles(Tester t) {
    return t
        .checkExpect(this.circleGuess.drawCircles(), new BesideImage(
            new CircleImage(10, "solid", Color.pink),
            new BesideImage(new CircleImage(10, "solid", Color.green),
                new BesideImage(new CircleImage(10, "solid", Color.blue),
                    new BesideImage(new CircleImage(10, "solid", Color.cyan), new EmptyImage())))))
        && t.checkExpect(this.circleGuess2.drawCircles(),
            new BesideImage(new CircleImage(10, "solid", Color.pink),
                new BesideImage(new CircleImage(10, "solid", Color.pink), new BesideImage(
                    new CircleImage(10, "solid", Color.blue),
                    new BesideImage(new CircleImage(10, "solid", Color.cyan), new EmptyImage())))));
  }

  // tests the drawCirclesReverse method for classes that implement ILoCircle
  boolean testDrawCirclesReverse(Tester t) {
    return t.checkExpect(this.circleGuess.drawCirclesReverse(),
        new BesideImage(
            new BesideImage(
                new BesideImage(
                    new BesideImage(new EmptyImage(), new CircleImage(10, "solid", Color.cyan)),
                    new CircleImage(10, "solid", Color.blue)),
                new CircleImage(10, "solid", Color.green)),
            new CircleImage(10, "solid", Color.pink)))
        && t.checkExpect(this.circleGuess2.drawCirclesReverse(),
            new BesideImage(
                new BesideImage(
                    new BesideImage(
                        new BesideImage(new EmptyImage(), new CircleImage(10, "solid", Color.cyan)),
                        new CircleImage(10, "solid", Color.blue)),
                    new CircleImage(10, "solid", Color.pink)),
                new CircleImage(10, "solid", Color.pink)));
  }

  // tests the drawGuessAndExactInexacts method for classes that implement
  // ILoCircle
  boolean testDrawGuessAndExactInexacts(Tester t) {
    return t
        .checkExpect(
            this.circleGuess.drawGuessesAndExactInexacts(4, 4, new EmptyImage(),
                new ConsLoExactInexact("2 3", new MtLoExactInexact())),
            new AboveAlignImage("left", new BesideImage(
                new BesideImage(
                    new BesideImage(
                        new BesideImage(
                            new BesideImage(new EmptyImage(),
                                new CircleImage(10, "solid", Color.cyan)),
                            new CircleImage(10, "solid", Color.blue)),
                        new CircleImage(10, "solid", Color.green)),
                    new CircleImage(10, "solid", Color.pink)),
                new TextImage("2 3", 19, Color.black)), new EmptyImage()))
        && t.checkExpect(
            this.circleGuess2.drawGuessesAndExactInexacts(4, 4, new EmptyImage(),
                new ConsLoExactInexact("0 0", new MtLoExactInexact())),
            new AboveAlignImage("left",
                new BesideImage(
                    new BesideImage(
                        new BesideImage(
                            new BesideImage(
                                new BesideImage(new EmptyImage(),
                                    new CircleImage(10, "solid", Color.cyan)),
                                new CircleImage(10, "solid", Color.blue)),
                            new CircleImage(10, "solid", Color.pink)),
                        new CircleImage(10, "solid", Color.pink)),
                    new TextImage("0 0", 19, Color.black)),
                new EmptyImage()));
  }

  // tests the drawNCircles method for the classes that implement ILoCircle
  boolean testDrawNCircles(Tester t) {
    return t.checkExpect(this.circleGuess.drawNCircles(0, new EmptyImage()), new EmptyImage())
        && t.checkExpect(this.circleGuess2.drawNCircles(1, new EmptyImage()),
            new BesideImage(new EmptyImage(), new CircleImage(10, "solid", Color.pink)));
  }

  // tests the deleteNCircles method for the classes that implement ILoCircle
  boolean testDeleteNCircles(Tester t) {
    return t.checkExpect(this.circleGuess.deleteNCircles(0), this.circleGuess)
        && t.checkExpect(this.circleGuess.deleteNCircles(1),
            new ConsLoCircle(new Circle(Color.green, "solid"),
                new ConsLoCircle(new Circle(Color.blue, "solid"),
                    new ConsLoCircle(new Circle(Color.cyan, "solid"), new MtLoCircle()))));
  }

  // tests the length method for classes that implement ILoColor
  boolean testLength(Tester t) {
    return t.checkExpect(this.mtcolor.length(), 0) && t.checkExpect(this.guess.length(), 4);
  }

  // tests the colorsToCircles method for classes that implement ILoColor
  boolean testColorsToCircles(Tester t) {
    return t.checkExpect(this.guess.colorsToCircles(), this.circleGuess)
        && t.checkExpect(this.guess2.colorsToCircles(), this.circleGuess2);
  }

  // tests the searchColor method for classes that implement ILoColor
  boolean testSearchColor(Tester t) {
    return t.checkExpect(this.guess.searchColor(1), Color.pink)
        && t.checkExpect(this.guess.searchColor(2), Color.green);
  }

  // tests the deleteFirst method for classes that implement ILoColor
  boolean testDeleteFirstILoColor(Tester t) {
    return t.checkExpect(this.guess.deleteFirst(),
        new ConsLoColor(Color.green,
            new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor()))))
        && t.checkExpect(this.guess2.deleteFirst(), new ConsLoColor(Color.pink,
            new ConsLoColor(Color.blue, new ConsLoColor(Color.cyan, new MtLoColor()))));
  }

  // tests the checkExact method for classes that implement ILoColor
  boolean testCheckExact(Tester t) {
    return t.checkExpect(this.guess.checkExact(this.solution), 0)
        && t.checkExpect(this.guess2.checkExact(this.solution), 0);
  }

  // tests the checkExactHelp method for classes that implement ILoColor
  boolean testCheckExactHelp(Tester t) {
    return t.checkExpect(this.guess.checkExactHelp(this.solution, Color.blue), 0)
        && t.checkExpect(this.guess2.checkExactHelp(this.solution, Color.pink), 1);
  }

  // tests the checkInexact method for classes that implement ILoColor
  boolean testCheckInexact(Tester t) {
    return t.checkExpect(this.guess.checkInexact(this.solution), 4)
        && t.checkExpect(this.guess2.checkInexact(this.solution), 3);
  }

  // tests the checkInexactHelp method for classes that implement ILoColor
  boolean testCheckInexactHelp(Tester t) {
    return t.checkExpect(
        this.guess.checkInexactHelp(this.guess, Color.blue, this.solution, this.mtcolor), 4)
        && t.checkExpect(
            this.guess.checkInexactHelp(this.guess2, Color.green, this.solution, this.guess), 5);
  }

  // tests the append method for classes that implement ILoColor
  boolean testAppend(Tester t) {
    return t.checkExpect(this.guess.append(this.mtcolor), this.guess)
        && t.checkExpect(this.guess.append(new ConsLoColor(Color.blue, new MtLoColor())),
            new ConsLoColor(Color.pink, new ConsLoColor(Color.green, new ConsLoColor(Color.blue,
                new ConsLoColor(Color.cyan, (new ConsLoColor(Color.blue, new MtLoColor())))))));
  }

  // tests the drawExactInexact method for classes that implement ILoExactInexact
  boolean testDrawExactInexact(Tester t) {
    return t.checkExpect(this.mtIE.drawExactInexact(), new EmptyImage()) && t.checkExpect(
        this.exactInexactExample.drawExactInexact(), new TextImage("1 2", 19, Color.black));
  }

  // tests the deleteFirst method for classes that implement ILoExactInexact
  boolean testDeleteFirstILoExactInexact(Tester t) {
    return t.checkExpect(this.mtIE.deleteFirst(), this.mtIE) && t
        .checkExpect(this.exactInexactExample.deleteFirst(), new ConsLoExactInexact("3 2", mtIE));
  }

  // test the isValid method in the utils class
  boolean testIsValid(Tester t) {
    return t.checkException(new IllegalArgumentException("Invalid number: 4"), new Utils(),
        "isValid", 4, 4, "Invalid number: ")
        && t.checkException(new IllegalArgumentException("Invalid number: 2"), new Utils(),
            "isValid", 2, 4, "Invalid number: ")
        && t.checkNoException(new Utils(), "isValid", 6, 4, "Too low");
  }

  // test the isValidListLength method in the utils class
  boolean testIsValidListLength(Tester t) {
    return t.checkException(new IllegalArgumentException("Invalid sequence length: 0"), new Utils(),
        "isValidListLength", new MtLoColor(), 0, 0, "Invalid sequence length: ")
        && t.checkNoException(new Utils(), "isValidListLength",
            new ConsLoColor(Color.red, new MtLoColor()), 1, 0, "Invalid sequence length: ");
  }
}
