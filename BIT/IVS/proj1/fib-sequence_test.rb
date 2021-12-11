require 'test/unit'
require_relative 'fib-sequence'

class FibonacciSequenceTest < Test::Unit::TestCase
  def setup
     @fibo = FibonacciSequence.new
  end
  
 #test metody reset
 def test_reset
 
 	assert_equal(true,@fibo.reset)
    assert_equal(nil,@fibo.current_idx)
 end 	

#test metody initialize
  def test_initialization
	assert_equal(0,@fibo.[](0))
    @fibo.next
    assert_equal(1,@fibo.current)
  end

#test metody next
  def test_next
    @fibo.[](0)
	assert_equal(1,@fibo.next)
	assert_equal(1,@fibo.next)
	@fibo.[](11)
	assert_equal(144,@fibo.next)
  end

#test metody current
  def test_currrent
	@fibo.[](0)
	assert_equal(0,@fibo.current)
	@fibo.[](10)
	assert_equal(55,@fibo.current)
  end

#test metody current_idx
  def test_current_idx
	assert_equal(nil,@fibo.current_idx)
    @fibo.[](-1)
    assert_equal(nil,@fibo.current_idx)
    @fibo.next
    assert_equal(0,@fibo.current_idx)
    @fibo.[](12)
    assert_equal(12,@fibo.current_idx)
  end

#test metody [].()
  def test_fibo_with_idx
	assert_equal(nil,@fibo.[](-1))
    assert_equal(0,@fibo.[](0))
    assert_equal(5,@fibo.[](5))
    assert_equal(6765,@fibo.[](20))
  end	

end
