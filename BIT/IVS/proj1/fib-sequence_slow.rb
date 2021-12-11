#Fibonacci sequence - slow method

class FibonacciSequence

  def initialize( f0 = 0, f1 = 1 )
    @seq = [f0, f1]
    self.reset
  end
  
  # nastavi radu do vychoziho stavu 
  def reset
    @idx = -1
    
    return true
  end
  
  # vrati dalsi fibonacciho cislo
  def next
  
  #spomalenie vypoctu jednotlivych clenov postupnosti,v kazdom kroku sa pocitaju odznova az po konkretne hladane
    @idx+=1
      for @idx in 2..@idx do    
      @seq[@idx] = @seq[@idx - 1] + @seq[@idx - 2]
      end
      return @seq[@idx] 
   end
  
  # aktualni fibonacciho cislo
  def current
    return @idx >= 0 ? @seq[@idx] : nil
  end
  
  # aktualni index (vraci nil, pokud se jeste nezacalo generovat)
  def current_idx
    return @idx >= 0 ? @idx : nil
  end
  
  # vrati fibonacciho cislo s danym indexem
  def [](n)
    return nil if n < 0
    return @seq[n] if n <= @idx
    
    while @idx < n
      self.next
     # print self.current
    end
    
    return self.current
  end




end


